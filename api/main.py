from fastapi import FastAPI, Depends, HTTPException, status, File, UploadFile
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from typing import List
import shutil
import os
from datetime import timedelta
import models
import schemas
import auth
from database import engine, get_db

models.Base.metadata.create_all(bind=engine)

app = FastAPI(title="EnviroCrime API")

# Configuração para upload de imagens
UPLOAD_DIR = "uploads"
if not os.path.exists(UPLOAD_DIR):
    os.makedirs(UPLOAD_DIR)

# Rotas de autenticação
@app.post("/token", response_model=schemas.Token)
async def login_for_access_token(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = auth.authenticate_user(db, form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token_expires = timedelta(minutes=auth.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = auth.create_access_token(
        data={"sub": user.email}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

# Rotas de usuário
@app.post("/users/", response_model=schemas.User)
def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    db_user = auth.get_user(db, email=user.email)
    if db_user:
        raise HTTPException(status_code=400, detail="Email already registered")
    hashed_password = auth.get_password_hash(user.password)
    db_user = models.User(
        name=user.name,
        cpf=user.cpf,
        email=user.email,
        phone=user.phone,
        password=hashed_password,
        is_admin=user.is_admin
    )
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user

@app.get("/users/me/", response_model=schemas.User)
def read_users_me(current_user: models.User = Depends(auth.get_current_active_user)):
    return current_user

@app.get("/users/", response_model=List[schemas.User])
def read_users(skip: int = 0, limit: int = 100, db: Session = Depends(get_db),
              current_user: models.User = Depends(auth.get_current_active_user)):
    if not current_user.is_admin:
        raise HTTPException(status_code=403, detail="Not authorized")
    users = db.query(models.User).offset(skip).limit(limit).all()
    return users

# Rotas de relatórios
@app.post("/reports/", response_model=schemas.Report)
def create_report(report: schemas.ReportCreate, db: Session = Depends(get_db),
                 current_user: models.User = Depends(auth.get_current_active_user)):
    db_report = models.Report(**report.dict(), user_id=current_user.id)
    db.add(db_report)
    db.commit()
    db.refresh(db_report)
    return db_report

@app.get("/reports/", response_model=List[schemas.Report])
def read_reports(skip: int = 0, limit: int = 100, db: Session = Depends(get_db),
                current_user: models.User = Depends(auth.get_current_active_user)):
    if current_user.is_admin:
        reports = db.query(models.Report).offset(skip).limit(limit).all()
    else:
        reports = db.query(models.Report).filter(models.Report.user_id == current_user.id).offset(skip).limit(limit).all()
    return reports

@app.get("/reports/{report_id}", response_model=schemas.Report)
def read_report(report_id: int, db: Session = Depends(get_db),
               current_user: models.User = Depends(auth.get_current_active_user)):
    report = db.query(models.Report).filter(models.Report.id == report_id).first()
    if report is None:
        raise HTTPException(status_code=404, detail="Report not found")
    if not current_user.is_admin and report.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized")
    return report

@app.put("/reports/{report_id}", response_model=schemas.Report)
def update_report(report_id: int, report: schemas.ReportCreate, db: Session = Depends(get_db),
                 current_user: models.User = Depends(auth.get_current_active_user)):
    db_report = db.query(models.Report).filter(models.Report.id == report_id).first()
    if db_report is None:
        raise HTTPException(status_code=404, detail="Report not found")
    if not current_user.is_admin and db_report.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized")
    
    for key, value in report.dict().items():
        setattr(db_report, key, value)
    
    db.commit()
    db.refresh(db_report)
    return db_report

# Rotas de imagens
@app.post("/reports/{report_id}/images/", response_model=schemas.ReportImage)
async def create_report_image(report_id: int, file: UploadFile = File(...), db: Session = Depends(get_db),
                            current_user: models.User = Depends(auth.get_current_active_user)):
    report = db.query(models.Report).filter(models.Report.id == report_id).first()
    if report is None:
        raise HTTPException(status_code=404, detail="Report not found")
    if not current_user.is_admin and report.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized")
    
    file_location = f"{UPLOAD_DIR}/{report_id}_{file.filename}"
    with open(file_location, "wb+") as file_object:
        shutil.copyfileobj(file.file, file_object)
    
    db_image = models.ReportImage(report_id=report_id, image_path=file_location)
    db.add(db_image)
    db.commit()
    db.refresh(db_image)
    return db_image

@app.get("/reports/{report_id}/images/", response_model=List[schemas.ReportImage])
def read_report_images(report_id: int, db: Session = Depends(get_db),
                      current_user: models.User = Depends(auth.get_current_active_user)):
    report = db.query(models.Report).filter(models.Report.id == report_id).first()
    if report is None:
        raise HTTPException(status_code=404, detail="Report not found")
    if not current_user.is_admin and report.user_id != current_user.id:
        raise HTTPException(status_code=403, detail="Not authorized")
    
    images = db.query(models.ReportImage).filter(models.ReportImage.report_id == report_id).all()
    return images 
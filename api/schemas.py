from pydantic import BaseModel, EmailStr
from typing import List, Optional
from datetime import datetime

class UserBase(BaseModel):
    name: str
    cpf: str
    email: EmailStr
    phone: str

class UserCreate(UserBase):
    password: str
    is_admin: bool = False

class User(UserBase):
    id: int
    is_admin: bool

    class Config:
        from_attributes = True

class ReportBase(BaseModel):
    title: str
    type: str
    description: str
    location: str
    anonymous: bool = False
    status: str = "pending"

class ReportCreate(ReportBase):
    pass

class ReportImageBase(BaseModel):
    image_path: str

class ReportImageCreate(ReportImageBase):
    pass

class ReportImage(ReportImageBase):
    id: int
    report_id: int

    class Config:
        from_attributes = True

class Report(ReportBase):
    id: int
    user_id: int
    created_at: datetime
    admin_notes: Optional[str] = None
    images: List[ReportImage] = []

    class Config:
        from_attributes = True

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    email: Optional[str] = None 
from sqlalchemy import Boolean, Column, ForeignKey, Integer, String, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime
from database import Base

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True)
    cpf = Column(String, unique=True, index=True)
    email = Column(String, unique=True, index=True)
    phone = Column(String)
    password = Column(String)
    is_admin = Column(Boolean, default=False)

    reports = relationship("Report", back_populates="user")

class Report(Base):
    __tablename__ = "reports"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, index=True)
    type = Column(String)
    description = Column(String)
    location = Column(String)
    user_id = Column(Integer, ForeignKey("users.id"))
    anonymous = Column(Boolean, default=False)
    status = Column(String)
    created_at = Column(DateTime, default=datetime.utcnow)
    admin_notes = Column(String)

    user = relationship("User", back_populates="reports")
    images = relationship("ReportImage", back_populates="report")

class ReportImage(Base):
    __tablename__ = "report_images"

    id = Column(Integer, primary_key=True, index=True)
    report_id = Column(Integer, ForeignKey("reports.id"))
    image_path = Column(String)

    report = relationship("Report", back_populates="images") 
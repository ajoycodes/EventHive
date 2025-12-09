# EventHive – Event Management System (Android + JavaFX)

EventHive is a multi-platform event management system built using **Android (Java)** and **JavaFX**.  
It supports multiple user roles (Admin, Organizer, User) and provides event creation, registration, and ticket tracking features across both platforms.

---

## 🚀 Features

### 🔐 Authentication
- User signup & login  
- Role-based dashboards (Admin / Organizer / User)

### 🎫 Event & Ticket Management
- Create, edit, delete events  
- Register for events  
- Auto-generated tickets  
- User event history  
- Organizers can view participant lists  

### 🛠 Admin Tools
- Manage users and organizers  
- Approve or remove events  

### 🖥 JavaFX Desktop App
- Mirror of Android features  
- Cleaner table-based UI  
- Easy event + user management  

---

## 🧰 Tech Stack
- **Java (Android + JavaFX)**
- **SQLite Database**
- **XML Layouts (Android)**

---


## 📁 Current Project Structure (Android)

```text
app/
 ├── java/
 │   └── com.example.myapplication
 │        ├── activities/        # Login, Register, Dashboard, etc.
 │        └── databases/         # DatabaseHelper (SQLite)
 ├── res/
 │   ├── layout/                 # activity_login.xml, activity_register.xml, activity_dashboard.xml, etc.
 │   └── values/                 # colors.xml, themes.xml, strings.xml
 └── AndroidManifest.xml
```
---

## 🛠 Setup Instructions

### 📱 Android
1. Open the project in **Android Studio**
2. Sync Gradle
3. Build & Run

### 🖥 JavaFX Desktop
1. Open the JavaFX folder in **IntelliJ**
2. Add JavaFX SDK (if required)
3. Run `Main.java`

---

## 📌 Future Enhancements
- QR-based ticket verification  
- Cloud-backed data sync  
- Push notifications  

---

## 👤 Author
**Ajoy Saha**  
Roll: 2207037

---

# 🛫 Runway Visualisation Tool

A JavaFX application for visualizing runway configurations with support for dynamic overlays and parameter adjustments.

---

## 🚀 Features
- Top-down and side-on runway views.
- Adjustable runway parameters.
- Visual overlays for RESA, TODA, TORA, and more.
- Supports zooming and panning
- Place down Obstacles 
- Multi User collaboartion - airport name must be the same
- User-friendly interface
- Print out Report
- Export XML

---

## 🔑 Login
Admin
- **username**: admin
- **password**: 123456
- 
Editor

- **username**: bob  
- **password**: 123456

Viewer

- **username**: taylor
- **password**: 123456

---

## 🧰 Requirements
- Java **19** (or compatible version).
- Maven **3.6+**.
- JavaFX **21** (managed via Maven).
- macOS, Windows

---

## 🔨 Build the project
Due to network use the key is required, using a provided json file, in the src/main/java/group50/network/Firebase.java
Write the SERVICE_ACCOUNT_JSON value as the directory of the key json file
```bash
private static final String SERVICE_ACCOUNT_JSON = "/[Somewhere]/runway-c8831-firebase-adminsdk-fbsvc-82b0a7918b.json"
```

To compile and run the project, run:
```bash
mvn clean package
java -jar target/Runway-1.0-SNAPSHOT-all.jar
```


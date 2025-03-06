# 🛫 Runway Visualisation Tool

A JavaFX application for visualizing runway configurations with support for dynamic overlays and parameter adjustments.

---

## 🚀 Features
- Top-down and side-on runway views.
- Adjustable runway parameters.
- Visual overlays for RESA, TODA, TORA, and more.
- Supports zooming, panning, and reset controls.
- User-friendly interface styled like classic console UIs.

---

## 🧰 Requirements
- Java **19** (or compatible version).
- Maven **3.6+**.
- JavaFX **21** (managed via Maven).
- macOS, Windows, or Linux.

---

## 🔨 Build the project
To compile and package the project, run:
```bash
mvn clean package -P shade
java --module-path /path/to/javafx-sdk-21/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     -jar target/Runway-1.0-SNAPSHOT-shaded.jar
```
-Recommended doesnt require download of SDKs
```bash
mvn javafx:run
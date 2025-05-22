# ImageRenamerApp

A simple Java Swing application for batch-renaming and cropping photos with zoom, pan, and keyboard shortcuts.

## About This Project

This application was developed during my internship at Stellantis as a practical tool to assist in the visualization and organization of industrial material samples.

In the automotive industry, each material sample is associated with a codified color and grain reference. The tool simplifies the manual process of renaming and cropping photographic samples by allowing fast, keyboard-driven input and high-resolution cropping. It was specifically designed to improve the workflow for reviewing and cataloging visual standards within the internal sample information in Excel.

## Features

- Fullscreen display of images (`.jpg`, `.jpeg`, `.png`)
- Not Compatable with `.HEIC` for iPhone pictures -> On your iPhone, open Settings → Camera → Formats.
  
Select “Most Compatible” (JPEG) instead of High Efficiency (HEIC).
- Zoom (mouse wheel) and pan (click-drag)  
- Fixed 1:1 square crop that you can reposition
- - **Resizable crop**: You can change the default crop size by modifying the `CROP_SIZE` value in the code  
  ```java
  private static final int CROP_SIZE = 200; // change this to adjust default crop size
- - Enter a **color code** and **grain code** for each image:
  - Color code: 3 uppercase letters (e.g. `ABC`)
  - Grain code: 1 uppercase letter + 3 digits (e.g. `Z123`)
- Optional “Flop” checkbox (picture taken from an angle)  
- Keyboard shortcuts:  
  - **Ctrl +D** → Delete current image  
  - **Ctrl + F** → Toggle “Flop”  
  - **Ctrl + G** → Rename & load next  
  - **Esc** → Exit application  

## Prerequisites

- Java 11 or higher installed  
- Eclipse IDE 2023-06 (or any recent Eclipse)  
- Image files in `images/` folder  

## Project Structure

    ImageRenamer/

          ├── images/ #Put your source photos here (jpg, png, etc.)

          ├── photos/ #Cropped & renamed photos are saved here

          └── src/

              └── app/

                    └── ImageRenamerApp.java #Main Java Swing application

## Usage

1. **Clone or download** this repository (see below).  
2. **Open** Eclipse and **import** the folder as an **Existing Java Project**.  
3. Ensure your source images live in the `images/` directory.  
4. **Run** `app.ImageRenamerApp` as a Java Application.  
5. Use the toolbar or shortcuts to rename, crop, delete, and advance.

## Contact

If you encounter any bugs or issues, feel free to report them or reach out via the contact form here:  
👉 [https://nico-rab.tech/#contact](https://nico-rab.tech/#contact)

## License

This project is released under the [MIT License](LICENSE).

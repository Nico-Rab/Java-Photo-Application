# ImageRenamerApp

A simple Java Swing application for batch-renaming and cropping photos with zoom, pan, and keyboard shortcuts.

## Features

- Fullscreen display of images (`.jpg`, `.jpeg`, `.png`)
- Not Compatable with HEIC for iPhone pictures -> On your iPhone, open Settings → Camera → Formats.
Select “Most Compatible” (JPEG) instead of High Efficiency (HEIC).
- HEIC Support coming soon!
- Zoom (mouse wheel) and pan (click-drag)  
- Fixed 1:1 square crop that you can reposition  
- Enter 3-letter color code and free-form grain code  
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

## License

This project is released under the MIT License.

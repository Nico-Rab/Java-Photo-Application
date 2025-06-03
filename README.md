# ColorCodePhotosApp

A simple Java Swing application for batch-renaming and cropping photos with zoom, pan, and keyboard shortcuts.

## About This Project

This application was developed during my internship at Stellantis as a practical tool to assist in the visualization and organization of industrial material samples.

In the automotive industry, each material sample is associated with a codified color and grain reference. The tool simplifies the manual process of renaming and cropping photographic samples by allowing fast, keyboard-driven input and cropping. It was specifically designed to improve the workflow for reviewing and cataloging visual standards.


## ⚙️ How It Works

- Reads photos from the `input_color/` folder  
- Displays them in fullscreen with zoom and pan functionality  
- Automatically overlays a **movable** 1:1 square crop zone  
- Lets the user enter:
  - A **color code** (`ABC`)
  - A **grain code** (`Z123`)
  - A checkbox for **“Flop”** (used when the photo is taken from an angle)
- When the user presses `Ctrl + G` or clicks **Rename & Next**:
  - The crop area is extracted and saved as a new image
  - The image is renamed using the format: `ABC-Z123.jpg` or `ABC-Z123_flop.jpg`
  - The renamed image is saved inside a subfolder of `output_color/` based on the **first two letters** of the color code
  - The original file is moved to `trash_color/`
### Example :
  - Color code: ABC
➜ Saved as: output_color/AB/ABC-Z123.jpg 

## Features

- Images formats (`.jpg`, `.jpeg`, `.png`)
- Not Compatable with `.HEIC` for iPhone pictures -> On your iPhone, open Settings → Camera → Formats. Select “Most Compatible” (JPEG) instead of High Efficiency (HEIC).
- Crop size slider (50–500 px)
- **Resizable crop**: If you don't like the default crop size, you can cahnge it by modifying the `CROP_SIZE` value in the code  
  ```java
  private static final int CROP_SIZE = 200; // change this to adjust default crop size
- Keyboard shortcuts:  
  - **Ctrl + D** → Delete current image  
  - **Ctrl + F** → Toggle “Flop”  
  - **Ctrl + G** → Rename & load next  
  - **Esc** → Exit application  

![Alt text](/project.gif)

## Prerequisites

- Java 11 or higher installed  
- Eclipse IDE 2023-06 (or any recent Eclipse)  
- Image files in `input_color/` folder  

## Project Structure

    ColorCodePhotosApp/

    ├── input_color/        # Put your source photos here (jpg, png, etc.)

    ├── output_color/       # Cropped & renamed photos are saved here (auto-created)

    ├── backup_color/       # Originals (and any “deleted” images) move here (auto-created)

    └── src/
    
        └── app/
        
            └── ColorCodePhotosApp.java  # Main Java Swing application

## Usage

1. **Clone or download** the zip file.  
2. Open Eclipse (e.g. Eclipse IDE for Java Developers).
- File → Import…
- Under General, choose Existing Projects into Workspace, then click Next.
- Select Archive file (if you downloaded the ZIP) or Select root directory (if you unzipped).
- If using ZIP: click Browse…, navigate to ColorCodePhotosApp.zip, and select it.
- If using unzipped folder: click Browse…, navigate to the folder ColorCodePhotosApp, and select it.
- Eclipse will detect the project. Make sure ColorCodePhotosApp is checked, then click Finish.  
3. Ensure your source images live in the `input_color/` directory.  
4. **Run** `app.ImageRenamerApp` as a Java Application.  
5. Use the toolbar or shortcuts to rename, crop, delete, and advance.

## Contact

If you encounter any bugs or issues, feel free to report them or reach out via my website contact form here:  
👉 [Send me a message](https://nico-rab.tech/#contact) !

## License

This project is released under the [MIT License](LICENSE).

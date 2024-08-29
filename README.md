# RodexCapstoneFE

## Overview
RodexCapstoneFE is an Android application built using Android Studio. This guide will help you set up your development environment and get the project running on your local machine.

## Prerequisites

Before you begin, ensure you have the following installed:

1. **Android Studio**: Download and install from [here](https://developer.android.com/studio).
2. **Java Development Kit (JDK)**: Android Studio requires a JDK. You can download it from [here](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).
3. **Git**: Install Git from [here](https://git-scm.com/downloads) to clone the repository.

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/RodexCapstoneFE.git
cd RodexCapstoneFE
```

### 2. Open the Project in Android Studio

Launch Android Studio.
Click on Open an existing Android Studio project.
Navigate to the directory where you cloned the project and select it.

### 3. Configure the SDK

Go to File > Project Structure.
Under SDK Location, ensure the path to your Android SDK is correct. If not, set it to the correct location.

### 4. Set Up an Android Emulator

Open the AVD Manager by clicking on the device icon in the toolbar or navigating to Tools > Device Manager.
Click on Create Virtual Device.
Choose a device definition (e.g., Pixel 5) and click Next.
Select a system image (e.g., API Level 30) and click Next.
Configure the emulator settings as needed, then click Finish.

### 5. Build and Run the Project

Select the emulator you created from the device dropdown menu.
Click on the Run button (green arrow) in the toolbar.
The project should build and run on the emulator.

### Troubleshooting

Build Errors: If you encounter build errors, try syncing the project with the Gradle files by clicking File > Sync Project with Gradle Files.
Emulator Issues: If the emulator doesn't start, try using a different system image or device definition.

# RainCheckApp
Rain Check App - Weather Forecast Application
RainCheck is a simple Android weather application built with Jetpack Compose. It allows users to check the weather for their current location for a selected date, with a smooth animated transition between the date selection and weather display screens.

## Features
* **Location-Based Weather:** Fetches weather data for the user's current location (requires location permissions).

* **Date Selection:** Users can select a specific date for which to check the weather.

* **Animated Screen Transitions:** Smooth transitions between the date picker and the main weather display.

* **Error Handling:** Displays user-friendly pop-up messages for errors, such as missing location permissions or data fetching issues.

* **Clean Architecture:** Utilizes a ViewModel (with Hilt for dependency injection) to manage UI state and logic.

# Setup and Run Instructions
1. **Prerequisites**
   * Android Studio (Ladybug 2024.2.2 or newer recommended)
   * An Android device or emulator running API Level 21 or higher.
   * A stable internet connection.
     
2. **Open Project in Android Studio**
   * Launch Android Studio.
   * Select Open an existing Android Studio project and navigate to the root directory of the cloned project.
     
3. **Sync Gradle Project**
   * Android Studio will automatically try to sync the Gradle project. If it doesn't, click on the Sync Project with Gradle Files button (usually an elephant icon with a down arrow) in the toolbar.
   * Ensure all dependencies are resolved successfully.

4. **Install Dagger Hilt**
   * This project uses Hilt for dependency injection. If you encounter issues related to hiltViewModel(), ensure Hilt is correctly set up in your build.gradle files as per the official Hilt documentation. The provided code assumes Hilt is already configured.
  
5. **Run the Application**
   * Connect an Android device to your computer via USB (ensure USB debugging is enabled on your device) or start an Android Emulator.
   * Select your target device/emulator from the toolbar dropdown.
   * Click the Run 'app' button (green play icon) in the toolbar.

6. **Grant Permissions**
   * The app requires ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions. When you first launch the app, a system dialog will appear asking for these permissions. You must grant them for the app to function correctly and fetch your current location. If permissions are denied, an error pop-up will be displayed.
  
# Known Limitations or Assumptions
* **UI Components as Placeholders**: PickerScreen, MainScreen, and SevenDayForecastScreen are simplified composables for illustrating screen transitions and data flow. A production-ready app would require a much more detailed and robust UI design for weather forecasts.

* **Hilt Setup Assumed:** The use of hiltViewModel() assumes that Hilt is correctly configured and set up in your project for dependency injection. If not, this part will cause compilation errors.

* **No Persistent Storage:** The app does not currently store any user preferences or historical weather data persistently.

* **Internet Connection:** An active internet connection is assumed for fetching location and weather data (once real APIs are integrated).

# UI Design Inspiration
The overall UI layout and aesthetic inspiration for this application are derived from the static HTML design provided in rain-check.html. This HTML file served as a visual guideline for the Jetpack Compose implementation, focusing on the mobile-first, centered layout with shadow and rounded corner effects.

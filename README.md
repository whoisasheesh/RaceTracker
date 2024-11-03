# RaceTracker
RaceTracker is an Android application designed to provide real-time updates on next to go races using the Neds API. Users can view a list of of next to go races in ascending order, filter by category, and keep track of race start times with a live countdown.

# Installation Instructions
- Clone the repository from GitHub: https://github.com/whoisasheesh/RaceTracker.git.
- Open the project in Android Studio.
- Ensure necessary SDKs and dependencies are installed.
- Build and Run the app on an Android device or emulator.

# Features
- **Next to Go Race Lists**: Displays the next 5 races sorted by advertised start time.
- **Automatic Race Removal**: Automatically removes races that are one minute past their advertised start time.
- **Category Filters**: Allows filtering of races by category type. Categories are:
  - Horse Racing
  - Harness Racing
  - Greyhound Racing
  - Users can deselect all filters to show races across all categories.
  - Shows race details, including the meeting name, race number, and a countdown timer past one minute after race started.
- **Modern UI with Jetpack Compose**: Built with a responsive UI using Jetpack Compose with state management and recomposability for UI smooth transition.
- **Kotlin as a core language**: Uses kotlin programming language for Business logic, Data models and Android-specific code.
- **MVVM Architecture**: Implements Model-View-ViewModel architecture, ensuring clear separation of concerns.

# Architecture
Race Tracker uses MVVM (Model-View-ViewModel) architecture with packaging structure inspired by Clean Architecture:
- **MVVM:** The app follows MVVM principles, allowing for separation of the UIs, business logic, and data management.
- **Clean Architecture Packaging**: While not modularized, the project is structured with packages to represent the layers of Clean Architecture.
  - **data**: Handles data sources, including the API service and Room database.
  - **domain**: Defines interfaces and usecases for repositoriy and viewmodel.
  - **presentation**: Contains UI elements like screens, app components, navigation graph, viewmodel, and other presentation logic.

# Package Structure
- **data**: Manages data sources and includes APIs, local storage, and data transformations.
- **di**: Dependency Injection setup with Hilt for managing app-wide dependencies.
- **domain**: Contains usecases and repository interfaces.
- **presentation**: Handles UI and ViewModel components.
- **components**: Contains reusable UI components.
- **graph**: Contains RootNavigation that manages navigation across screens.
- **screens**: Hosts individual screens like RaceListScreen, SplashScreen, and NoInternetScreen.
- **viewmodel**: Contains ViewModel interface and its implementation to manage manage UI-related data and logic.

# Technologies Used
- **Kotlin**: Primary programming language.
- **Jetpack Compose**: For building declarative UI components.
- **Room**: Local database for caching race data.
- **Retrofit & OkHttp**: For networking and API calls.
- **Dagger Hilt**: For Dependency Injection.
- **JUnit4** and **Mockito**: For unit testing.

# Dependencies
The following primary dependencies are configured to support the app’s architecture and feature set:
- **AndroidX Core, AppCompat, Material Components**: For essential UI and utility features.
- **Navigation Compose**: For handling screen navigation.
- **Retrofit and OkHttp**: For REST API interactions.
- **Room**: For local data storage and caching.
- **Coil**: For horse riding gif image loading in Compose for Splash Screen.
- **Accompanist**: For additional Compose utilities like SwipeRefresh.

# UI Flow
- **Splash Screen**: Initializes the app with loading progress bar with a horse riding gif to provide user with interactive UI experience.
- **Race List Screen**: Displays the list of Next to Go races, with filtering options based on race category, **Horse**, **Harness** and **Greyhound**.
**No Internet Screen**: Displays the no internet screen as soon as there is no network connectivity.

# User Interaction
- **Next to Go Races Filtering**: User can select or deselect race categories via filtering checkboxes.
- **Swipe to Refresh** : User can swipe to refresh within the Race List screen to fetch the latest upcoming races.
- **Automatic Refresh**: The race list is updated as soon as the races are past one minute from advertised time, removing past races and updating based on the selected category.

# API Integration and Data Handling Logic
 **Api endpoint**: GET https://api.neds.com.au/rest/v1/racing/?method=nextraces&count=10
- Data is fetched in JSON format and stored locally using Room database.
- Even though the fetched races count is 10, only 5 next to go races are shown by fetching it from the local database.
- When the race disappear after one minute past advertised time, the updated list will be fetched from the local database.

# Testing
Covers the unit tests to validate its core functionality, particularly focusing on the Race List View Model to ensure robust data handling and filtering logic.
- Verify that the RaceListViewModel correctly observe connectivity, fetches and filters next to go races.

# Accessibility
The app includes accessibility features, such as labels and descriptions, to ensure it is usable for people with visual impairments. Voice-over functionality and screen reader support have been implemented where necessary.

TODO:- Adding screen record video with App Use to demonstrate scalability and font scale changes and accessibility.

Note: The built-in screen recorder captures multimedia audio but does not record the accessibility channel, which is used for TalkBack speech output.

**Accessing TalkBack for VoiceOver Navigation:**
To use TalkBack accessibility in the Race Tracker app, follow these steps:
1.	Open Settings on your device.
2.	Navigate to Accessibility.
3.	Enable TalkBack by toggling it on.
4.	Select the Race Tracker app from the app list.
5.	Navigate through the app; TalkBack will provide spoken descriptions of the app components.


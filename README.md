NammaNala – Smart Canal Monitoring System 🌊📍
NammaNala is a modern Android application developed using Kotlin + Jetpack Compose + Firebase to help monitor canal infrastructure, water flow, and irrigation-related issues in rural areas.
The application allows users to:


report canal issues with real-time GPS location,


capture and upload images,


monitor water levels,


log water arrival status for villages,


and visualize reports directly on Google Maps.


The project is designed with a dark modern UI, optimized for low-bandwidth environments, and follows a scalable architecture using MVVM.

🚀 Features
📸 Smart Issue Reporting
Users can:


capture leak/blockage images,


automatically attach GPS coordinates,


upload reports to Firebase,


and view them instantly inside the app.


Supported issue types:


Leak


Silt


Blockage


Illegal Water Usage



🗺️ Live Canal Map Integration
The application uses Google Maps SDK to:


display reported canal issues,


show real-time marker locations,


resolve nearest village/locality,


and show estimated distance from nearby areas.


Features:


Marker clustering behavior


Dynamic report plotting


Locality resolution using Geocoder


Interactive map navigation



💧 Water Status Feed
Village-level water arrival tracking system.
Users can:


log water arrival updates,


add village names,


view timestamped feed updates,


and monitor irrigation distribution.



🌊 Water Level Monitoring
Displays:


canal flow percentages,


sector-wise flow levels,


animated progress indicators,


and system-wide water statistics.


Flow categories:


High Flow


Medium Flow


Low Flow


Critical Low



🛠️ Maintenance Tracker
Tracks:


cleaning schedules,


inspection status,


maintenance progress,


and canal repair activity.


Includes:


status chips,


progress indicators,


and maintenance summaries.



🌑 Modern Dark UI
The app features:


dark theme dashboard,


glassmorphism-inspired cards,


responsive layouts,


modern typography,


and optimized UI for Android devices.



🔥 Firebase Integration
Integrated Firebase services:


Firebase Firestore


Firebase Storage


Firebase Authentication ready architecture


Used for:


storing reports,


image uploads,


water status logs,


and cloud synchronization.



📦 Tech Stack
TechnologyUsageKotlinAndroid DevelopmentJetpack ComposeModern UIFirebase FirestoreCloud DatabaseFirebase StorageImage UploadsGoogle Maps SDKMap VisualizationMVVM ArchitectureApp StructureMaterial 3UI Components

📱 Success Criteria Covered
✅ Distance from nearest village/locality displayed
✅ Water status feed includes timestamps
✅ UI optimized for low-bandwidth usage
✅ Real-time GPS location support
✅ Image-based issue reporting
✅ Firebase cloud synchronization
✅ Modern responsive dark UI

🧠 Architecture
The application follows:


MVVM Architecture


Repository Pattern


State-driven UI using Compose


Structure:
UI Layer↓ViewModel↓Repository↓Firebase Services

📸 Screens Included


Main Dashboard


Canal Map Screen


Water Status Feed


Maintenance Tracker


Water Level Monitor


Camera Upload Workflow



⚙️ Setup Instructions
1. Clone Repository
git clone <your_repo_url>

2. Open in Android Studio
Use:


Android Studio Hedgehog or newer


JDK 17 recommended



3. Firebase Setup
Add your:
google-services.json
inside:
app/
Enable:


Firestore Database


Firebase Storage


Google Maps SDK



4. Add Maps API Key
Inside:
AndroidManifest.xml
add:
<meta-data    android:name="com.google.android.geo.API_KEY"    android:value="YOUR_API_KEY"/>

5. Run Project
Connect emulator or Android device and run:
Run ▶ App

📷 Permissions Used
CAMERAACCESS_FINE_LOCATIONACCESS_COARSE_LOCATIONINTERNET

🎯 Future Improvements


Offline report caching


AI-based leak detection


Admin dashboard


Push notifications


Live water analytics


Real-time IoT integration


Role-based authentication



👨‍💻 Developed By
Anurag Sharma
B.Tech CSE Student
Android & Full Stack Developer

📄 License
This project is developed for educational and research purposes.

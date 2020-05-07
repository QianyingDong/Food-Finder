# Food-Finder
This is an Android application.  

The link of the introduction of this App is https://www.youtube.com/watch?v=NVwYs3rNzJk 
Feel free to watch it :)  

Because the server(firebase) has stopped, this application couldn't run now.


This application runs on mobile phone and only supports Android SDK version from 21 to 28. 

It makes use of two kinds of sensors, including GPS and camera. Therefore, location permission should be granted at the beginning of APP.

It can be directly compiled by Android Studio Software and data store at a Firebase project.

Four main functions are designed in this application, including signin & register, recommended food trail, add restaurants to my food trail, and view my trails.

### 1. Sign in & Register 
New user need to register for a new account with an email address and a password of at least 6 digits. 

Old user can directly sign in. 

Two pages can be switched to each other.

### 2. Recommended food trail 
Once user signed in, their current location will be shown on the map. 

If user clicks on "Find Food Trails", three food trails will be shown on the map. 

Each trail has different color and details of restaurants can be seen through the colorful button on the left hand side. 

Each trail consists of three nearby restaurants which have the highest ratings within 2 km. 

### 3. Add restaurants to my food trail 
If user clicks on "Find Nearby Restaurants", user can find the restaurant in which they are eating from the nearby restaurants.

If user clicks on the marker, a short description of restaurant name, address, rating will be shown.

If user clicks on the short description, a dialog will pop up for further instruction. User can now choose to add this restaurant to a new food trail or an existing one.

User needs to set the name of the new food trail, take a photo of the food or the restaurant and give the restaurant a rating for a food trail.

### 4. View my trails 
If user clicks on the floating button on the top of the map with the footprint image, user profile will show.

It will contain all the food trails that has been created by this user and it will show a text to recommend user to create one if there is no trail.

Each trail can be clicked and details of added restaurants will appear. 

Especially, taken photo will be shown in the detail page as well.

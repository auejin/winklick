# Winklick
is an open source touch input system using pupil for android smartphones.
And this is made by [Auejin Ham](https://fb.com/auejin), a sophomore in Kwanak High School, Seoul, South Korea.

# Algorithm Structure
Google Presentation : [Click here to see](https://docs.google.com/presentation/d/1-lDESAHk1FEUKRgrNjmKZ9gnl_Uo8wq3_Nsei2fDRaQ/pub?start=true&loop=false&delayms=60000)

Test Video : [Click here to see](https://youtu.be/Eldtnx98beA)

# Awards and Honors
* Silver Medal on Korean Olympiad of Informatics (Project Section, 2015)
* First Award on Seoul Science Project Presentation Competition (2016)
* Finalist Medal on Intel International Science and Engineering Fair (2016)
* Participated on Google Science Fair (2016)

# How to Use This
1. Hold your phone vertically(in portrait way).
2. Touch **Run Winklick**, and swipe out three steps of disclamer pages.
3. Touch **Run Winklick** again, and keep looking at the front camera of the smartphone.
4. Follow the Instruction Voice and pink popup on the screen. Make sure your face isn't out of image. The Instruction voice will guide you about this as well.
5. After the machine learning process is over, you can move a cursor on the screen, by looking top, bottom, left, right side of your phone. You can inject touch event on the location of the cursor, by simply winking to the front camera.

# How to Install This
1. Recompile this with Eclipse (or Android Studio)
2. Or just find Winklick_ISEF.apk on /bin folder.
3. Install apk file on your smartphone.
4. Enjoy.

# Disclaimer
1. Make sure to avoid backlights and keep your site bright.
2. Look at the center of the screen and please try not to move your face during learning process. The Instruction voice will guide you about this.
3. Face with Eyeglasses cannot be recognized. Sorry.

# Trouble Shooting

###Q. How Can I make touch event with this application
1. First, make sure your device is rooted. Sorry I cannot automize this process :(
1. Install **SuperSU** application on your device.
2. On the first activation, you will see a popup of SuperSU. Allow the root access of Winklick.
3. Also, if your version of Android is API 23(marshmallow) or higher, you can see another popup when you touch **Run Winklick**. Allow the permission of camera useage.

###Q. The only thing I can do is typing Qwerty keyboard with my eye. How can I use this outside this keyboard?
The reason that keyboard screen is shown is because your device isn't rooted yet.
You can use Winklick as ordinary touch input system only on rooted devices.


###Q. What is **Standard Eyes**?
**Standard Eyes** are targets that Winklick detects for movements of a cursor on the screen.
There are two kinds of Standard Eyes, **Standard Eye for Wink Detection** and **Standard Eye for Eye Gaze Detection**.
The former is used to detect whether user is pressing on the cursor or not.
The latter is used to determine the direction of cursor movements.
Each Standard Eye can be choosen between **Left Eye** and **Right Eye** independently.


###Q. How can I change language into English?
1. Touch second button lableled **설정**　
2. Touch **Language**
3. Select **English**

###Q. The cursor moves too slow. Gotta go fast!
Go to **Settings** and change **Cursor Speed** to more higher one.

###Q. The Algorithm execution cycle is too slow.
Go to **Settings** and change **Algorithm Execution Speed** to more lower one.

###Q. I want to make my eye image area transparent. It covers my screen.
Go to **Settings** and disable **Show Input Image** check button.



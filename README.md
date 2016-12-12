# Winklick
is an open source touch input system using pupil movements for android devices.
This entire system is made by Auejin Ham, a sophomore in Kwanak High School, Seoul, South Korea.

Contact me via [Facebook](https://fb.com/auejin), [Linkedin](https://linkedin.com/in/auejin), [Naver Blog (Korean)](http://haj990108.blog.me/).
Or mail me at [gmail](mailto:vingtee@gmail.com), [naver](mailto:haj990108@naver.com).

# Algorithm Structure
Google Presentation : [Click here to see](https://docs.google.com/presentation/d/1-lDESAHk1FEUKRgrNjmKZ9gnl_Uo8wq3_Nsei2fDRaQ/pub?start=true&loop=false&delayms=60000)

Test Video : [Click here to see](https://youtu.be/Eldtnx98beA)

# Awards and Honors
* Silver Medal on Korean Olympiad of Informatics (Project Section, 2015)
* First Award on Seoul Science Project Presentation Competition (2016)
* Finalist Medal on Intel International Science and Engineering Fair (2016)
* Participated on Google Science Fair (2016)

# How to Use This
1. Hold your smartphone vertically. If your device is tablet, hold your device horizontally.
2. Touch **Run Winklick**.
3. If this is the first activation and your version of Android is **API 23(marshmallow) or higher**, you will see two popups for the access permission of camera useage and drawing over other apps. To use full Winklick system, **ALLOW EVERY PERMISSION**.
4. Swipe out three steps of disclamer pages. Follow these disclamers for better recognition.
5. After swiping the three pages, touch **Run Winklick** again, and keep looking at the front camera of your device.
6. Follow the learning process written in a pink popup on the screen. TTS will read aloud those instructions. If Winklick service can't find your full face on input image, the instruction voice will notice you.
7. After the machine learning process is completed, now you can type on the keyboard screen using Winklick system. Move a cursor on the screen, by looking top, bottom, left, right side of your phone. You can inject press and release event on the location of the cursor, by simply winking to the front camera.
8. You can use Winklick input system on every application (just as an ordinary touch input system), only after your device is rooted. Check out the process on **[here](https://github.com/auejin/winklick#q-how-can-i-make-touch-event-outside-the-keyboard-screen)**.

# How to Install This
1. Compile this with Eclipse (or Android Studio)
2. Or just find Winklick_ISEF.apk on /bin folder.
3. Install apk file on your smartphone.
4. Enjoy.

# Disclaimer
1. Make sure to avoid bright lights on your back and keep your area bright.
2. Look at the center of the screen and please try not to move your face during learning process. The Instruction voice will guide you about this.
3. Face with Eyeglasses cannot be recognized yet. (This will be available on the next project!)

# Trouble Shooting

###Q. The only thing I can do is typing Qwerty keyboard with my eye. How can I use this outside this keyboard screen?
The reason that keyboard screen is shown is because your device isn't rooted yet.
You can use Winklick just like an ordinary touch input system if your device is rooted.

###Q. How Can I make touch event outside the keyboard screen?
1. First, make sure your device is rooted. Sorry I cannot automize this process :(
1. Install **SuperSU** application on your device.
2. On the first activation, you will see a popup of SuperSU. Allow the root access of Winklick.

###Q. What is **Standard Eyes**?
**Standard Eyes** are targets that Winklick detects for movements of a cursor on the screen.
There are two kinds of Standard Eyes, **Standard Eye for Wink Detection** and **Standard Eye for Eye Gaze Detection**.
The former is used to detect whether user is pressing on the cursor or not.
The latter is used to determine the direction of cursor movements.
Each Standard Eye can be choosen between **Left Eye** and **Right Eye** independently.

###Q. How can I change language into English?
1. Touch the second button lableled **설정**　
2. Touch **Language**
3. Select **English**

###Q. The cursor moves too slow. Gotta go fast!
Go to **Settings** and change **Cursor Speed** to more higher one.

###Q. The Algorithm execution cycle is too slow.
Go to **Settings** and change **Algorithm Execution Speed** to more lower one.

###Q. I want to make my eye image area transparent. It covers my screen.
Go to **Settings** and disable **Show Input Image** check button.



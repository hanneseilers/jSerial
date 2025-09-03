jSerial
===========

jSerial is a library for connecting serial usb converter to your Java application.
jSerial uses different open source libraries from that you can choose and was primarily build to connect to ftdi to serial converter chips. but it also works with regular serial hardware ports.

Used libraries
--------------
* jSerialComm (from: Fazecast, Licence: LGPL V3, Apache v2)
* jd2xx (Licence: LGPL v2, BSD)
* yad2xx (Licence: LGPL v3)

* rxtx (Licence: LGPL v2.1 ) !deprecated, not used anymore!

Installation
------------
Simply use the .jar file. Normally jSerialComm should work on all operating systems without anything to do.

Except for Linux users, execute inside your terminal:

    sudo usermod -a -G uucp username
    sudo usermod -a -G dialout username
    sudo usermod -a -G lock username
    sudo usermod -a -G tty username

Troubleshooting
---------------
If jSerialComm is not working, copy the lib-sources directory into your project root directory. So other libraries can be loaded.

Licence
-------
You can use jSerial for private use as you want. You're also free to modify jSerial depending on your own needs.
Take care of the licences of the used libraries (see above) and also refer to LICENCE file for details about the jSerial licence.

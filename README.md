jSerial
===========

jSerial is a library for connecting serial usb converter to your Java application.
jSerial uses different open source libraries from that you can choose and was primarily build to connect to ftdi to serial converter chips. but it also works with regular serial hardware ports.

Used libraries
--------------
* jd2xx (Licence: LGPL v2, BSD)
* yad2xx (Licence: LGPL v3)
* rxtx (Licence: LGPL v2.1 )

Installation
------------
Build jSerial.jar using ant build script or use the ones from release and include it into your project.
Copy lib dependencies from source or from release achrive file into your peoject and include external lib jar files.

On Ubuntu x64 systems you also needed to install librxtx-java from ubuntu repository.
If jFTDIserial didn't work because of not found library dlls (.so on linux), install the corresponding library inside your Java runtime installation path.

Licence
-------
You can use jSerial for private use as you want. You're also free to modify jSerial depending on your own needs.
Take care of the licences of the used libraries (see above) and also refer to LICENCE file for details about the jSerial licence.

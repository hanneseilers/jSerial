jFTDIserial
===========

Java library for connecting ftdi serial usb converter.
jFTDIserial uses different open source libraries from that you can choose and was primarily build to connect to ftdi to serial converter chips. but it also works with regular serial hardware ports.

Used libraries
--------------
* jd2xx (Licence: LGPL v2, BSD)
* yad2xx (Licence: LGPL v3)
* rxtx (Licence: LGPL v2.1 )

Installation
------------
Normally no installation (except of Java runtime) is needed.
On Ubuntu x64 systems you also needed to install librxtx-java from ubuntu repository.
If jFTDIserial didn't work because of not found library dlls (.so on linux), install the corresponding library inside your Java runtime installation path.

Licence
-------
You can use jFTDIserial for private use as you want. You're also free to modify jFTDIserial depending on your own needs.
Take care of the licences of the used libraries (see above) and also refer to LICENCE file for details about the jFTDIserial licence.

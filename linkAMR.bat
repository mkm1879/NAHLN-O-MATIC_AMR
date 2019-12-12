cd amr\bin
jar -c --file=..\..\mods\amr.jar --main-class=edu.clemson.lph.amr.NahlnOMaticAMR *  
cd ..\..\lphutils\bin
jar -c --file=..\..\mods\lphutils.jar *  
cd ..\..\lphdialogs\bin
jar -c --file=..\..\mods\lphdialogs.jar *  

cd ..\..
jar -c -M -f NOM.zip NAHLN-O-MATIC_AMR.bat jre lib mods InBox OutBox ErrorsBox Config.xlsx LICENSE.txt
pause
erase /Q/F .\jrelast
pause
rename jre jrelast

jlink --no-header-files --no-man-pages --compress=2 --add-modules java.base,java.desktop,java.logging,java.net.http,java.prefs,java.se,java.xml,java.xml.crypto,jdk.charsets --output jre
pause

rem UNUSED modules java.rmi,java.scripting,jdk.accessibility,jdk.aot,jdk.attach,jdk.internal.vm.ci,jdk.internal.vm.compiler,jdk.internal.vm.compiler.management,jdk.jartool,jdk.javadoc,jdk.jcmd,jdk.jconsole,jdk.jdeps,jdk.jdi,jdk.jdwp.agent,jdk.jfr,jdk.jlink,jdk.jshell,jdk.compiler,jdk.crypto.cryptoki,jdk.crypto.ec,jdk.crypto.mscapi,jdk.dynalink,jdk.editpad,jdk.hotspot.agent,jdk.httpserver,jdk.internal.ed,jdk.internal.jvmstat,jdk.internal.le,jdk.internal.opt,jdk.jsobject,jdk.jstatd,jdk.localedata,jdk.management,jdk.management.agent,jdk.management.jfr,jdk.naming.dns,jdk.naming.rmi,jdk.net,jdk.pack,jdk.rmic,jdk.scripting.nashorn,jdk.scripting.nashorn.shell,jdk.sctp,jdk.security.auth,jdk.security.jgss,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom,jdk.zipfs,java.management,java.management.rmi,java.security.jgss,java.security.sasl,java.smartcardio,java.compiler,java.datatransfer,java.sql,java.sql.rowset,java.transaction.xa,java.instrument,java.naming
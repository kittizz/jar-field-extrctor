import java.io.File
import java.io.IO.println
import java.net.URLClassLoader
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 3) {
        println("การใช้งาน: java -jar program.jar <jar file> <class> <Field>")
        println("ตัวอย่าง: java -jar program.jar nukkit.jar cn.nukkit.network.protocol.ProtocolInfo MINECRAFT_VERSION_NETWORK")
        exitProcess(1)
    }

    val jarPath = args[0]
    val className = args[1]
    val fieldName = args[2]

    try {
        // สร้าง URL สำหรับโหลด .jar file
        val jarUrl = File(jarPath).toURI().toURL()


        // สร้าง ClassLoader เพื่อโหลดคลาสจาก JAR
        URLClassLoader(arrayOf(jarUrl), ClassLoader.getSystemClassLoader()).use { classLoader:URLClassLoader ->
            // โหลดคลาสตามที่ระบุ
            val loadedClass = classLoader.loadClass(className)

            // อ่านค่า field ที่ต้องการด้วย reflection
            val field = loadedClass.getDeclaredField(fieldName)
            field.isAccessible = true
            val fieldValue = field.get(null)

            println("$fieldValue")
            exitProcess(0)
        }
    } catch (e: Exception) {
        println("เกิดข้อผิดพลาด: ${e.message}")
        e.printStackTrace()
        exitProcess(1)
    }
}
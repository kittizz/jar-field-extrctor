import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 3) {
        println("การใช้งาน: java -jar program.jar <jar file> <class> <field>")
        println("ตัวอย่าง: java -jar program.jar nukkit.jar cn.nukkit.network.protocol.ProtocolInfo MINECRAFT_VERSION_NETWORK")
        exitProcess(1)
    }

    val jarPath = args[0]
    val className = args[1]
    val fieldName = args[2]

    // พยายามใช้วิธี Reflection ก่อน
    try {
        val result = extractFieldByReflection(jarPath, className, fieldName)
        println(result)
        exitProcess(0)
    } catch (t: Throwable) {  // ใช้ Throwable แทน Exception เพื่อจับ Error ด้วย

        try {
            val result = extractFieldByASM(jarPath, className, fieldName)
            println(result)
            exitProcess(0)
        } catch (e: Exception) {
            println("การใช้ Reflection ล้มเหลว: ${t.message}")

            println("การใช้ ASM ล้มเหลวเช่นกัน: ${e.message}")
            e.printStackTrace()
            exitProcess(1)
        }




    }


}

/**
 * ดึงค่า field โดยใช้ Reflection API
 */
fun extractFieldByReflection(jarPath: String, className: String, fieldName: String): String {
    val jarUrl = File(jarPath).toURI().toURL()

    // พยายามสร้าง ClassLoader ที่จะทำงานได้แม้มี dependency ขาดหาย
    val urlClassLoader = URLClassLoader(arrayOf(jarUrl), null)  // ไม่ใช้ parent classloader

    try {
        // โหลดคลาสตามที่ระบุ
        val loadedClass = urlClassLoader.loadClass(className)

        // อ่านค่า field ที่ต้องการด้วย reflection
        val field = loadedClass.getDeclaredField(fieldName)
        field.isAccessible = true
        val fieldValue = field.get(null)

        return fieldValue?.toString() ?: "null"
    } finally {
        urlClassLoader.close()
    }
}

/**
 * ดึงค่า field โดยใช้ ASM (bytecode manipulation)
 */
fun extractFieldByASM(jarPath: String, className: String, fieldName: String): String {
    val classPath = className.replace(".", "/")
    val jarFile = JarFile(jarPath)

    try {
        // หา entry ของคลาสที่ต้องการ
        val classEntry = jarFile.getJarEntry("$classPath.class")
            ?: throw Exception("ไม่พบคลาส: $className")

        // อ่าน class file เป็น byte array
        val classBytes = jarFile.getInputStream(classEntry).readBytes()

        // ใช้ ASM ClassReader เพื่ออ่าน class file
        val cr = ClassReader(classBytes)

        var fieldValue: String? = null

        // ใช้ ClassVisitor เพื่อค้นหา field
        cr.accept(object : ClassVisitor(Opcodes.ASM9) {
            override fun visitField(
                access: Int,
                name: String,
                descriptor: String,
                signature: String?,
                value: Any?
            ): FieldVisitor? {
                // ตรวจสอบว่าเป็น field ที่เราต้องการหรือไม่
                if (name == fieldName) {
                    // ถ้าเป็นค่าคงที่ (constant value) เราจะได้ค่ากลับมาเลย
                    fieldValue = value?.toString()
                }
                return super.visitField(access, name, descriptor, signature, value)
            }
        }, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG)

        return fieldValue ?: throw Exception("ไม่พบ field $fieldName ใน $className หรือไม่ใช่ constant value")
    } finally {
        jarFile.close()
    }
}
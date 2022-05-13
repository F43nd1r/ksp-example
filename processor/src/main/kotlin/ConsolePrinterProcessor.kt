import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName

@OptIn(KotlinPoetKspPreview::class)
class ConsolePrinterProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotatedClasses = resolver.getSymbolsWithAnnotation(GenerateConsolePrinter::class.java.name).filterIsInstance<KSClassDeclaration>()
        for (annotatedClass in annotatedClasses) {
            val className = annotatedClass.simpleName.asString() + "Impl"
            val typeBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(annotatedClass.toClassName())

            typeBuilder.addFunctions(annotatedClass.getDeclaredFunctions().map { function ->
                val name = function.simpleName.asString()
                FunSpec.builder(name)
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("println(%S)", name)
                    .build()
            }.toList())

            val fileSpec = FileSpec.builder(annotatedClass.packageName.asString(), className)
                .addType(typeBuilder.build())
                .build()
            codeGenerator.createNewFile(Dependencies(false, annotatedClass.containingFile!!), fileSpec.packageName, fileSpec.name)
                .writer()
                .use { fileSpec.writeTo(it) }
        }
        return emptyList()
    }
}

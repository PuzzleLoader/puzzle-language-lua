package com.github.puzzle.loader.lua.globals;

import com.github.puzzle.core.PuzzleRegistries;
import com.github.puzzle.loader.launch.Piece;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.ImmutablePair;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import org.greenrobot.eventbus.Subscribe;
import org.luaj.vm2.LuaValue;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class LPuzzleEventBusUtil {

    public static Map<String, LuaValue> valueMap = new HashMap<>();
    public static int generatedClassCount = 0;

    public void registerEvent(LuaValue func, String eventClass) {
        Pair<String, byte[]> clazzPair = createEventClass(eventClass, func);
        Class<?> clazz = Piece.classLoader.defineClass(clazzPair.getLeft(), clazzPair.getRight());
        try {
            PuzzleRegistries.EVENT_BUS.register(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Pair<String, byte[]> createEventClass(String eventClass, LuaValue function) {
        String clazzName = "lua/generated_classes/GeneratedClassNum" + generatedClassCount;

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC,
                clazzName,
                null,
                "java/lang/Object",
                new String[0]
        );
        valueMap.put(clazzName, function);
        createBlankConstructor(cw);
        createEventMethod(clazzName, cw, "L"+eventClass.replaceAll("\\.", "/")+";", function);

        // Make Class
//        new File("lua/generated_classes/").mkdirs();
//        try {
//            FileOutputStream stream = new FileOutputStream(clazzName + ".class");
//            stream.write(cw.toByteArray());
//            stream.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        generatedClassCount++;
        return new ImmutablePair<>(clazzName.replaceAll("/", "."), cw.toByteArray());
    }

    private static void createBlankConstructor(ClassWriter cw) {
        MethodVisitor constructor = cw.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
        );
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(1, 1);
    }

    private static void createEventMethod(String className, ClassWriter cw, String methodClass, LuaValue function) {
        MethodVisitor register = cw.visitMethod(
                Opcodes.ACC_PUBLIC,
                "register",
                "("+methodClass+")V",
                null,
                null
        );
        register.visitAnnotation(toDescriptor(Subscribe.class), true);
        register.visitCode();
        Label L0Label = new Label();
        Label L1Label = new Label();
        Label L2Label = new Label();
        register.visitLabel(L0Label);
        register.visitLineNumber(10, L0Label);
        register.visitFieldInsn(Opcodes.GETSTATIC, "com/github/puzzle/loader/lua/globals/LPuzzleEventBusUtil", "valueMap", "Ljava/util/Map;");
        register.visitLdcInsn(className);
        register.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
        register.visitTypeInsn(Opcodes.CHECKCAST, "org/luaj/vm2/LuaValue");
        register.visitVarInsn(Opcodes.ALOAD, 1);
        register.visitMethodInsn(Opcodes.INVOKESTATIC, "org/luaj/vm2/lib/jse/CoerceJavaToLua", "coerce", "(Ljava/lang/Object;)Lorg/luaj/vm2/LuaValue;");
        register.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/luaj/vm2/LuaValue", "call", "(Lorg/luaj/vm2/LuaValue;)Lorg/luaj/vm2/LuaValue;");
        register.visitInsn(Opcodes.POP);
        register.visitLabel(L1Label);
        register.visitLineNumber(11, L1Label);
        register.visitInsn(Opcodes.RETURN);
        register.visitLabel(L2Label);
        register.visitLocalVariable("this", "Lcom/github/puzzle/loader/lua/TestClass;", null, L0Label, L2Label, 0);
        register.visitLocalVariable("event", "Lcom/github/puzzle/game/events/OnRegisterBlockEvent;", null, L0Label, L2Label, 1);
        register.visitMaxs(2, 2);
    }

    private static String toDescriptor(Class<?> c) {
        return "L" + c.getName().replaceAll("\\.", "/") + ";";
    }

}

/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/03/01 15:34:28
 *
 * NukkitLauncher/NukkitLauncher/NukkitBootstrap.java
 */

package cn.mcres.karlatemp.nukkit.launcher;

import cn.mcres.karlatemp.unsafe.Unsafe;
import org.objectweb.asm.*;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class NukkitBootstrap {
    public static Field bootstrap(ClassLoader loader) throws IOException, NoSuchFieldException {
        try (final InputStream stream = loader.getResourceAsStream("cn/nukkit/Server.class")) {
            ClassReader reader = new ClassReader(Objects.requireNonNull(stream));
            ClassWriter out = new ClassWriter(0);
            String genName = "qkwe" + UUID.randomUUID();
            out.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, genName, "Ljava/lang/Class;", null, null);
            AtomicReference<String> fieldPath = new AtomicReference<>();
            reader.accept(new ClassVisitor(Opcodes.ASM5) {
                @Override
                public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                    if ((access & Opcodes.ACC_STATIC) == 0)
                        if (descriptor.equals("Lcn/nukkit/plugin/PluginManager;")) {
                            fieldPath.set(name);
                        }
                    return null;
                }
            }, 0);
            reader.accept(new ClassVisitor(Opcodes.ASM5, out) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    return new MethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            if (opcode == Opcodes.INVOKEVIRTUAL) {
                                if (owner.equals("cn/nukkit/plugin/PluginManager")) {
                                    // System.out.println("In PM");
                                    if (name.equals("loadPlugins")) {
                                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                        super.visitInsn(Opcodes.POP);
                                        super.visitVarInsn(Opcodes.ALOAD, 0);
                                        super.visitFieldInsn(Opcodes.GETFIELD, "cn/nukkit/Server", fieldPath.toString(), "Lcn/nukkit/plugin/PluginManager;");
                                        super.visitLdcInsn("jrt$.");
                                        super.visitMethodInsn(opcode, owner, "loadPlugin", "(Ljava/lang/String;)Lcn/nukkit/plugin/Plugin;", false);
                                        return;
                                    } else if (name.equals("registerInterface")) {
                                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                        super.visitInsn(Opcodes.POP);
                                        super.visitVarInsn(Opcodes.ALOAD, 0);
                                        super.visitFieldInsn(Opcodes.GETFIELD, "cn/nukkit/Server", fieldPath.toString(), "Lcn/nukkit/plugin/PluginManager;");
                                        super.visitFieldInsn(Opcodes.GETSTATIC, "cn/nukkit/Server", genName, "Ljava/lang/Class;");
                                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                        return;
                                    }
                                }
                            }
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                        }
                    };
                }
            }, 0);
            byte[] code = out.toByteArray();
            try (FileOutputStream os = new FileOutputStream("out/Server.class")) {
                os.write(code);
            }
            // new ClassReader(code).accept(new TraceClassVisitor(null, new Textifier(), new PrintWriter(System.out)), 0);
            return Unsafe.getUnsafe().defineClass(null, code, 0, code.length, loader, null)
                    .getDeclaredField(genName);
        }
    }

    public static void bootstrap(
            ResourceFinder finder,
            ClassLoader loader,
            ClassLoader nukkitLoader,
            File pluginFile) throws IOException, NoSuchFieldException {
        Field field = bootstrap(nukkitLoader);
        final Unsafe unsafe = Unsafe.getUnsafe();
        unsafe.ensureClassInitialized(field.getDeclaringClass());
        unsafe.putReference(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), PluginLoaderMirror.class);
        PluginLoaderMirror.mirror = new NukkitLauncher(finder, loader, pluginFile);
    }
}

package org.spongepowered.asm.mixin.transformer;

import lombok.val;
import ru.xdark.launcher.ClassFileTransformer;
import ru.xdark.launcher.ClassTransformation;

public final class TelekineticProxy implements ClassFileTransformer {

    private static int transformersCount = 0;
    private static final MixinTransformer TRANSFORMER = new MixinTransformer();
    private final boolean active = transformersCount++ == 0;

    @Override
    public void transform(ClassTransformation transformation) throws Exception {
        if (!active) return;
        val bytes = transformation.getClassBytes();
        if (bytes != null) {
            transformation.setTransformationResult(TRANSFORMER.transformClassBytes(transformation.getOriginalClassName(), transformation.getTransformedClassName(), bytes));
        }
    }


}

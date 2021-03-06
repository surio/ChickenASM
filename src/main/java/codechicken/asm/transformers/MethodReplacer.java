package codechicken.asm.transformers;

import codechicken.asm.*;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.Set;

import static codechicken.asm.ASMHelper.logger;

/**
 * Replaces a specific needle with a specific replacement.
 * Can replace more than one needle.
 */
public class MethodReplacer extends MethodTransformer {

    public ASMBlock needle;
    public ASMBlock replacement;

    /**
     * Replaces the provided needle with the provided replacement.
     *
     * @param method      The method to replace in.
     * @param needle      The needle to replace.
     * @param replacement The replacement to apply.
     */
    public MethodReplacer(@Nonnull ObfMapping method, @Nonnull ASMBlock needle, @Nonnull ASMBlock replacement) {
        super(method);
        this.needle = needle;
        this.replacement = replacement;
    }

    /**
     * Replaces the provided needle with the provided replacement.
     *
     * @param method      The method to replace in.
     * @param needle      The needle to replace.
     * @param replacement The replacement to apply.
     */
    public MethodReplacer(@Nonnull ObfMapping method, @Nonnull InsnList needle, @Nonnull InsnList replacement) {
        this(method, new ASMBlock(needle), new ASMBlock(replacement));
    }

    @Override
    public void addMethodsToSort(Set<ObfMapping> set) {
        set.add(method);
    }

    @Override
    public void transform(MethodNode mv) {
        for (InsnListSection key : InsnComparator.findN(mv.instructions, needle.list)) {
            ModularASMTransformer.log("Replacing method " + method + " @ " + key.start + " - " + key.end);
            ASMBlock replaceBlock = replacement.copy().pullLabels(needle.applyLabels(key));
            key.insert(replaceBlock.list.list);
        }
    }
}

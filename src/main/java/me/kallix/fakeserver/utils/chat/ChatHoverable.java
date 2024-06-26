package me.kallix.fakeserver.utils.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatHoverable {
    private final ChatHoverable.EnumHoverAction a;
    private final IChatBaseComponent b;

    public ChatHoverable(ChatHoverable.EnumHoverAction var1, IChatBaseComponent var2) {
        this.a = var1;
        this.b = var2;
    }

    public ChatHoverable.EnumHoverAction a() {
        return this.a;
    }

    public IChatBaseComponent b() {
        return this.b;
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (var1 != null && this.getClass() == var1.getClass()) {
            ChatHoverable var2 = (ChatHoverable)var1;
            if (this.a != var2.a) {
                return false;
            } else {
                if (this.b != null) {
                    if (!this.b.equals(var2.b)) {
                        return false;
                    }
                } else if (var2.b != null) {
                    return false;
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "HoverEvent{action=" + this.a + ", value='" + this.b + '\'' + '}';
    }

    public int hashCode() {
        int var1 = this.a.hashCode();
        var1 = 31 * var1 + (this.b != null ? this.b.hashCode() : 0);
        return var1;
    }

    public static enum EnumHoverAction {
        SHOW_TEXT("show_text", true),
        SHOW_ACHIEVEMENT("show_achievement", true),
        SHOW_ITEM("show_item", true),
        SHOW_ENTITY("show_entity", true);

        private static final Map<String, EnumHoverAction> e = new ConcurrentHashMap<>();
        private final boolean f;
        private final String g;

        private EnumHoverAction(String var3, boolean var4) {
            this.g = var3;
            this.f = var4;
        }

        public boolean a() {
            return this.f;
        }

        public String b() {
            return this.g;
        }

        public static ChatHoverable.EnumHoverAction a(String var0) {
            return (ChatHoverable.EnumHoverAction)e.get(var0);
        }

        static {
            ChatHoverable.EnumHoverAction[] var0 = values();
            int var1 = var0.length;

            for(int var2 = 0; var2 < var1; ++var2) {
                ChatHoverable.EnumHoverAction var3 = var0[var2];
                e.put(var3.b(), var3);
            }

        }
    }
}

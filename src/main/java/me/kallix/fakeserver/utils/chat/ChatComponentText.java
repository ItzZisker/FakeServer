package me.kallix.fakeserver.utils.chat;

import java.util.Iterator;

public class ChatComponentText extends ChatBaseComponent {

    private final String b;

    public ChatComponentText(String var1) {
        this.b = var1;
    }

    public String g() {
        return this.b;
    }

    public String getText() {
        return this.b;
    }

    public ChatComponentText f() {
        ChatComponentText var1 = new ChatComponentText(this.b);
        var1.setChatModifier(this.getChatModifier().clone());
        Iterator var2 = this.a().iterator();

        while(var2.hasNext()) {
            IChatBaseComponent var3 = (IChatBaseComponent)var2.next();
            var1.addSibling(var3.f());
        }

        return var1;
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof ChatComponentText)) {
            return false;
        } else {
            ChatComponentText var2 = (ChatComponentText)var1;
            return this.b.equals(var2.g()) && super.equals(var1);
        }
    }

    public String toString() {
        return "TextComponent{text='" + this.b + '\'' + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
    }
}

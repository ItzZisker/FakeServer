package me.kallix.fakeserver.utils.chat;

import java.util.Iterator;

public class ChatComponentScore extends ChatBaseComponent {

    private final String b;
    private final String c;
    private String d = "";

    public ChatComponentScore(String var1, String var2) {
        this.b = var1;
        this.c = var2;
    }

    public String g() {
        return this.b;
    }

    public String h() {
        return this.c;
    }

    public void b(String var1) {
        this.d = var1;
    }

    public String getText() {
        return this.d;
    }

    public ChatComponentScore f() {

        ChatComponentScore var1 = new ChatComponentScore(this.b, this.c);

        var1.b(this.d);
        var1.setChatModifier(this.getChatModifier().clone());

        for (IChatBaseComponent var3 : this.a()) {
            var1.addSibling(var3.f());
        }
        return var1;
    }

    public boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof ChatComponentScore)) {
            return false;
        } else {
            ChatComponentScore var2 = (ChatComponentScore)var1;
            return this.b.equals(var2.b) && this.c.equals(var2.c) && super.equals(var1);
        }
    }

    public String toString() {
        return "ScoreComponent{name='" + this.b + '\'' + "objective='" + this.c + '\'' + ", siblings=" + this.a + ", style=" + this.getChatModifier() + '}';
    }
}

package me.kallix.fakeserver.utils.chat;

import java.util.List;

public interface IChatBaseComponent extends Iterable<IChatBaseComponent> {

    IChatBaseComponent setChatModifier(ChatModifier var1);

    ChatModifier getChatModifier();

    IChatBaseComponent a(String var1);

    IChatBaseComponent addSibling(IChatBaseComponent var1);

    String getText();

    String c();

    List<IChatBaseComponent> a();

    IChatBaseComponent f();
}

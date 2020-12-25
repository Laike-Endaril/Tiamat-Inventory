package moe.plushie.rpg_framework.api.mail;

import com.mojang.authlib.GameProfile;
import moe.plushie.rpg_framework.api.core.IIdentifier;

import java.util.ArrayList;

public interface IMailSystemManager {

    public IMailSystem getMailSystem(IIdentifier identifier);

    public IMailSystem[] getMailSystems();

    public String[] getMailSystemNames();

    public void onSendMailMessages(IMailSendCallback callback, GameProfile[] receivers, IMailMessage mailMessage);

    public static interface IMailSendCallback {

        public void onMailResult(ArrayList<GameProfile> success, ArrayList<GameProfile> failed);
    }
}

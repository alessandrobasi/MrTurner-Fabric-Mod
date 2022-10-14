package it.alessandrobasi.mrturner.mixin;

import com.mojang.brigadier.ParseResults;
import it.alessandrobasi.mrturner.client.MrturnerClient;
import it.alessandrobasi.mrturner.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.time.Instant;
import java.util.List;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinSendCommand {

    private static String MSG = "{player} rispetto la tua {condizione} e per questo non {azione}, " +
            "ma facendo valere la mia autorità da Staffer, {azione} lo stesso";

    private boolean sendTroll(String player, String azione, String condizione) {
        assert MinecraftClient.getInstance().player != null;

        assert MinecraftClient.getInstance().world != null;

        List<AbstractClientPlayerEntity> l = MinecraftClient.getInstance().world.getPlayers();

        List<String> q = l.stream().map(p -> p.getGameProfile().getName()).toList();

        // il nome di chi viene punito è lo stesso del player
        if(MinecraftClient.getInstance().player.getGameProfile().getName().equals(player))
            return false;

        // controllo se il player punito è online
        if(q.stream().noneMatch(p -> p.equals(player)))
            return false;

        MinecraftClient.getInstance().player.sendChatMessage(
                MSG.replace("{player}", player)
                        .replace("{condizione}", condizione)
                        .replace("{azione}", azione), null);
        return true;

    }

    private boolean checkCommand(String command){
        String[] args = command.split(" ");

        if(args.length < 2)
            return false;

        switch (args[0]) {
            case "ban", "tempban":
                return sendTroll(args[1], "ti banno", "scelta di gioco");
            case "mute", "tempmute":
                return sendTroll(args[1], "ti muto", "opinione");
            case "kick":
                return sendTroll(args[1], "ti caccio", "opinione");
            default:
                return false;
        }


    }
    @Shadow
    public abstract ArgumentSignatureDataMap signArguments(MessageMetadata signer, ParseResults<CommandSource> parseResults, @Nullable Text preview, LastSeenMessageList lastSeenMessages);

    @Inject(
            cancellable = true,
            method = "sendCommand(Ljava/lang/String;)Z",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at =
                @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V")
            )
    public void onCommandSend(String command, CallbackInfoReturnable<Boolean> cir, LastSeenMessageList.Acknowledgment acknowledgment) {

        if(!checkCommand(command))
            return;

        cir.setReturnValue(false);
        cir.cancel();

        (new Thread(() -> {
            assert MinecraftClient.getInstance().player != null;
            try {
                Thread.sleep(ConfigManager.getConfig().getDelay());
                MinecraftClient.getInstance().player.networkHandler.sendPacket(new CommandExecutionC2SPacket(command, Instant.now(), 0L, ArgumentSignatureDataMap.EMPTY, false, acknowledgment));
            } catch (Exception e) {
                e.printStackTrace();
            }

        })).start();

    }

    @Inject(
            method = "sendCommandInternal(Ljava/lang/String;Lnet/minecraft/text/Text;)V",
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"
            )
    )
    public void onCommandSendInternal(
            String command, Text preview, CallbackInfo ci, ParseResults parseResults, MessageMetadata messageMetadata, LastSeenMessageList.Acknowledgment acknowledgment) {

        if(!checkCommand(command))
            return;

        ci.cancel();

        (new Thread(() -> {
            assert MinecraftClient.getInstance().player != null;
            try {
                Thread.sleep(ConfigManager.getConfig().getDelay());
                ArgumentSignatureDataMap argumentSignatureDataMap = this.signArguments(messageMetadata, parseResults, preview, acknowledgment.lastSeen());
                MinecraftClient.getInstance().player.networkHandler.sendPacket(
                        new CommandExecutionC2SPacket(command, messageMetadata.timestamp(), messageMetadata.salt(), argumentSignatureDataMap, preview != null, acknowledgment)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

        })).start();
    }

}
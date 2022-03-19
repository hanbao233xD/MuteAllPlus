package club.yuanpi.muteall;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.*;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static club.yuanpi.muteall.utils.*;


public class AuthCode implements Listener {
    public static List<String> authed = new LinkedList();

    String oncecode;
    String authcode;
    Player player;
    boolean th = false;
    Thread codeThread = new Thread(() -> {
        while (true) {
            try {
                String code;
                code = utils.getRandomString(8, 8);
                oncecode = authcode;
                Bukkit.broadcastMessage(ChatColor.GREEN + "验证码已更新：" +
                        oncecode);
                Thread.sleep(1000 * 30);
            } catch (Exception e) {
            }
        }
    });


    @EventHandler
    public void onLogin(LoginEvent event) {
        if (!th) {
            codeThread.start();
            th = true;
        }
        player = event.getPlayer();
        utils.senderr(event.getPlayer(), "请输入验证码：" + oncecode);
        utils.sendok(event.getPlayer(), "【注册/登录后再输入】");

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        authed.remove(event.getPlayer().getName());

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!authed.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) throws MalformedURLException {

        Runtime rt = Runtime.getRuntime();
        String message = event.getMessage();
        Player player = event.getPlayer();
        String playerName = player.getName();

        String commandType = Character.toString(event.getMessage().charAt(0));
        String command = event.getMessage().substring(1);
        File logs = new File("logs\\latest.log");
        if (Objects.equals(message, oncecode)) {
            if (!authed.contains(playerName)) {
                log("[MuteAll+][" + playerName + "]验证成功");
                authed.add(playerName);
                sendok(player, "验证成功！");
                event.setCancelled(true);
            }
        } else if (!authed.contains(playerName)) {
            senderr(player, "验证码输入错误！");
        }

        if (!authed.contains(playerName)) {
            event.setCancelled(true);

        }


    }
}

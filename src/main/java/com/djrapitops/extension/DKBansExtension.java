/*
 * Copyright (c) 2019 Plan | Player Analytics
 *
 * The MIT License(MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.djrapitops.extension;

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import ch.dkrieger.bansystem.lib.player.history.BanType;
import ch.dkrieger.bansystem.lib.stats.NetworkStats;
import ch.dkrieger.bansystem.lib.stats.PlayerStats;
import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.FormatType;
import com.djrapitops.plan.extension.NotReadyException;
import com.djrapitops.plan.extension.annotation.*;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * DataExtension for DKBans.
 *
 * @author Vankka
 */
@PluginInfo(name = "DKBans", iconName = "gavel", iconFamily = Family.SOLID, color = Color.RED)
public class DKBansExtension implements DataExtension {

    public DKBansExtension() {
    }

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.PLAYER_JOIN,
                CallEvents.PLAYER_LEAVE,
                CallEvents.SERVER_EXTENSION_REGISTER,
                CallEvents.SERVER_PERIODICAL
        };
    }

    private double calculatePercentage(double input1, double input2) {
        if (input1 == 0 || input2 == 0) {
            return 0.0;
        }

        return input1 / input2;
    }

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

    /**
     * Strip method independent of any platform.
     */
    private String strip(String input) {
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    private BanSystem getBanSystem() {
        return BanSystem.getInstance();
    }

    @BooleanProvider(
            text = "Banned",
            description = "Is the player banned on DKBans",
            priority = 100,
            conditionName = "banned",
            iconName = "gavel",
            iconColor = Color.RED
    )
    public boolean isBanned(UUID playerUUID) {
        return getDKPlayer(playerUUID).isBanned(BanType.NETWORK);
    }

    @Conditional("banned")
    @StringProvider(
            text = "Operator",
            description = "Who banned the player",
            priority = 99,
            iconName = "user",
            iconColor = Color.RED,
            playerName = true
    )
    public String banIssuer(UUID playerUUID) {
        return strip(getDKPlayer(playerUUID).getBan(BanType.NETWORK).getStaffName());
    }

    @Conditional("banned")
    @NumberProvider(
            text = "Date",
            description = "When the ban was issued",
            priority = 98,
            iconName = "calendar",
            iconFamily = Family.REGULAR,
            iconColor = Color.RED,
            format = FormatType.DATE_YEAR
    )
    public long banIssueDate(UUID playerUUID) {
        return getDKPlayer(playerUUID).getBan(BanType.NETWORK).getTimeStamp();
    }

    @Conditional("banned")
    @BooleanProvider(
            text = "Will Expire",
            description = "Is the ban permanent",
            priority = 97,
            conditionName = "ban_expires",
            iconName = "calendar-check",
            iconFamily = Family.REGULAR,
            iconColor = Color.RED
    )
    public boolean banWillExpire(UUID playerUUID) {
        return getDKPlayer(playerUUID).getBan(BanType.NETWORK).getTimeOut() > 0;
    }

    @Conditional("ban_expires")
    @NumberProvider(
            text = "Ends",
            description = "When the ban expires",
            priority = 96,
            iconName = "calendar-check",
            iconFamily = Family.REGULAR,
            iconColor = Color.RED,
            format = FormatType.DATE_YEAR
    )
    public long banExpireDate(UUID playerUUID) {
        return getDKPlayer(playerUUID).getBan(BanType.NETWORK).getTimeOut();
    }

    @Conditional("banned")
    @StringProvider(
            text = "Reason",
            description = "Why the ban was issued",
            priority = 95,
            iconName = "comment",
            iconFamily = Family.REGULAR,
            iconColor = Color.RED
    )
    public String banReason(UUID playerUUID) {
        return getDKPlayer(playerUUID).getBan(BanType.NETWORK).getReason();
    }

    @BooleanProvider(
            text = "Muted",
            description = "Is the player muted on DKBans",
            priority = 50,
            conditionName = "muted",
            iconName = "bell-slash",
            iconColor = Color.DEEP_ORANGE,
            showInPlayerTable = true
    )
    public boolean isMuted(UUID playerUUID) {
        return getDKPlayer(playerUUID).isBanned(BanType.CHAT);
    }

    @Conditional("muted")
    @StringProvider(
            text = "Operator",
            description = "Who muted the player",
            priority = 49,
            iconName = "user",
            iconColor = Color.DEEP_ORANGE,
            playerName = true
    )
    public String muteIssuer(UUID playerUUID) {
        return strip(getDKPlayer(playerUUID).getBan(BanType.CHAT).getStaffName());
    }

    @Conditional("muted")
    @NumberProvider(
            text = "Date",
            description = "When the mute was issued",
            priority = 48,
            iconName = "calendar",
            iconFamily = Family.REGULAR,
            iconColor = Color.DEEP_ORANGE,
            format = FormatType.DATE_YEAR
    )
    public long muteIssueDate(UUID playerUUID) {
        return getDKPlayer(playerUUID).getBan(BanType.CHAT).getTimeStamp();
    }

    @Conditional("muted")
    @BooleanProvider(
            text = "Will Expire",
            description = "Is the mute permanent",
            priority = 47,
            conditionName = "mute_expires",
            iconName = "calendar-check",
            iconFamily = Family.REGULAR,
            iconColor = Color.DEEP_ORANGE
    )
    public boolean muteWillExpire(UUID playerUUID) {
        return getDKPlayer(playerUUID).getBan(BanType.CHAT).getTimeOut() > 0;
    }

    @Conditional("mute_expires")
    @NumberProvider(
            text = "Ends",
            description = "When the mute expires",
            priority = 46,
            iconName = "calendar-check",
            iconFamily = Family.REGULAR,
            iconColor = Color.DEEP_ORANGE,
            format = FormatType.DATE_YEAR
    )
    public long muteExpireDate(UUID playerUUID) {
        return getDKPlayer(playerUUID).getBan(BanType.CHAT).getTimeOut();
    }

    @Conditional("muted")
    @StringProvider(
            text = "Reason",
            description = "Why the mute was issued",
            priority = 45,
            iconName = "comment",
            iconFamily = Family.REGULAR,
            iconColor = Color.DEEP_ORANGE
    )
    public String muteReason(UUID playerUUID) {
        return getDKPlayer(playerUUID).getBan(BanType.CHAT).getReason();
    }

    @NumberProvider(
            text = "Bans",
            description = "How many bans the player has on DKBans",
            priority = 25,
            iconName = "flag",
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long bans(UUID playerUUID) {
        return getDKPlayer(playerUUID).getStats().getBans();
    }

    @NumberProvider(
            text = "Unbans",
            description = "How many unbans the player has on DKBans",
            priority = 24,
            iconName = "calendar-check",
            iconFamily = Family.REGULAR,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long unbans(UUID playerUUID) {
        return getDKPlayer(playerUUID).getStats().getUnbans();
    }

    private NetworkPlayer getDKPlayer(UUID playerUUID) {
        return Optional.ofNullable(getBanSystem())
                .map(BanSystem::getPlayerManager)
                .map(players -> players.getPlayer(playerUUID))
                .orElseThrow(NotReadyException::new);
    }

    @NumberProvider(
            text = "Mutes",
            description = "How many mutes the player has on DKBans",
            priority = 23,
            iconName = "volume-mute",
            iconFamily = Family.SOLID,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long mutes(UUID playerUUID) {
        return getDKPlayer(playerUUID).getStats().getMutes();
    }

    @NumberProvider(
            text = "Warnings",
            description = "How many warnings the player has on DKBans",
            priority = 22,
            iconName = "exclamation",
            iconFamily = Family.SOLID,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long warnings(UUID playerUUID) {
        return getDKPlayer(playerUUID).getStats().getWarns();
    }

    @NumberProvider(
            text = "Reports",
            description = "How many reports the player has on DKBans",
            priority = 21,
            iconName = "exclamation",
            iconFamily = Family.SOLID,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long reports(UUID playerUUID) {
        return getDKPlayer(playerUUID).getStats().getReports();
    }

    @PercentageProvider(
            text = "Report accept ratio",
            description = "The accepted/denied ratio for reports on DKBans",
            priority = 20,
            iconName = "exclamation",
            iconFamily = Family.SOLID,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public double reportPercentage(UUID playerUUID) {
        PlayerStats stats = getDKPlayer(playerUUID).getStats();
        long accepted = stats.getReportsAccepted();
        return calculatePercentage(stats.getReportsDenied() + accepted, accepted);
    }

    // Server

    @NumberProvider(
            text = "Bans",
            description = "Total amount of bans on DKBans",
            priority = 25,
            iconName = "flag",
            iconColor = Color.AMBER
    )
    public long bans() {
        return getBanSystem().getNetworkStats().getBans();
    }

    @NumberProvider(
            text = "Unbans",
            description = "Total amount of unbans on DKBans",
            priority = 24,
            iconName = "calendar-check",
            iconFamily = Family.REGULAR,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long unbans() {
        return getBanSystem().getNetworkStats().getUnbans();
    }

    @NumberProvider(
            text = "Mutes",
            description = "Total amount of mutes on DKBans",
            priority = 23,
            iconName = "volume-mute",
            iconFamily = Family.SOLID,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long mutes() {
        return getBanSystem().getNetworkStats().getMutes();
    }

    @NumberProvider(
            text = "Warnings",
            description = "Total amount of warnings on DKBans",
            priority = 22,
            iconName = "exclamation",
            iconFamily = Family.SOLID,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long warnings() {
        return getBanSystem().getNetworkStats().getWarns();
    }

    @NumberProvider(
            text = "Reports",
            description = "Total amount of reports on DKBans",
            priority = 21,
            iconName = "exclamation",
            iconFamily = Family.SOLID,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public long reports() {
        return getBanSystem().getNetworkStats().getReports();
    }

    @PercentageProvider(
            text = "Report accept ratio",
            description = "The global accepted/denied ratio for reports on DKBans",
            priority = 20,
            iconName = "exclamation",
            iconFamily = Family.SOLID,
            iconColor = Color.AMBER,
            showInPlayerTable = true
    )
    public double reportPercentage() {
        NetworkStats stats = getBanSystem().getNetworkStats();
        long accepted = stats.getReportsAccepted();
        return calculatePercentage(stats.getReportsDenied() + accepted, accepted);
    }


}
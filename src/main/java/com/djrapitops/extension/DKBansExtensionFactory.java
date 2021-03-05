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

import com.djrapitops.plan.extension.Caller;
import com.djrapitops.plan.extension.DataExtension;

import java.util.Optional;

/**
 * Factory for the DKBans DataExtension.
 *
 * @author AuroraLS3
 * @author Vankka
 */
public class DKBansExtensionFactory {

    private boolean isAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean isAvailable() {
        return isAvailable("ch.dkrieger.bansystem.lib.BanSystem");
    }

    public Optional<DataExtension> createExtension() {
        if (isAvailable()) {
            return Optional.of(new DKBansExtension());
        }
        return Optional.empty();
    }

    public void registerListener(Caller caller) {
        // Additional classes used to avoid NoClassDefFoundErrors
        if (isAvailable("org.bukkit.event.EventHandler")) {
            DKBBukkitListenerFactory.createBukkitListener(caller).register();
        }
        if (isAvailable("net.md_5.bungee.event.EventHandler")) {
            DKBBungeeListenerFactory.createBungeeListener(caller).register();
        }
    }
}
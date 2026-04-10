# PvPCore

![Build](https://github.com/thisaintdomi/PvPCore/actions/workflows/build.yml/badge.svg)
![MC](https://img.shields.io/badge/Minecraft-1.21.4%2B-brightgreen)
![Java](https://img.shields.io/badge/Java-21-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Egy plugin amibe bele raktam mindent amit egy FullPVP vagy BoxPVP szervernek szüksége van. Nem kell 6 különböző plugin — CombatTag, KillStreak, Bounty, Kit, Prestige, Leaderboard, saját gazdaság — mind egyben, egy pluginban.

---

## Funkciók

**CombatTag** — Harcba lépéskor mindkét játékos megkapja a tagot. Ha valaki kilép tag alatt, a konzol végrehajt egy parancsot (alapból `kill`). Az idő és a parancs config-ban állítható.

**KillStreak** — Minden ölés számlálva van. 5, 10, 25+ öléssorozatnál broadcast megy a szerveren hangeffekttel és coin jutalommal. A küszöbök, üzenetek, hangok és jutalmak mind `killstreak.yml`-ben vannak.

**BountySystem** — Játékosok fejdíjat tűzhetnek ki egymás fejére coinból. Az összeget automatikusan megkapja aki megöli a célpontot, és server-wide broadcast megy ki.

**KitSelector** — GUI-ban lehet kitet választani, de `/kit <nev>`-vel is megy. Minden kitnek van saját cooldownja. Prestige szint csökkenti a cooldownt, adminok meg teljesen megkerülhetik.

**StatsTracker** — Ölések, halálok, K/D, jelenlegi sorozat, legjobb sorozat, prestige szint, coinok — mind mentve játékosonként YAML fájlba. Restart után is megmarad minden.

**Leaderboard** — `/leaderboard` chatben, illetve hologram in-worldben. Ha a szerveren van DecentHolograms, azt használja. Ha nincs, invisible ArmorStandokkal oldja meg — nem kell külső plugin.

**PrestigeSystem** — Ha egy játékos eléri a szükséges ölésszámot, `/prestige claim`-mel nullázhatja a statját és kap egy display tagot, coin jutalmat, és csökkentett kit cooldownt véglegesen.

**EconomySystem** — Saját coin rendszer, nem kell Vault. Ölésért coin jár, sorozatnál bónusz szorzóval. Halálakor a zsebből kiesik egy százalék amit az ölő felvesz.

**ArenaManager** — Regisztrált ládákat automatikusan tölt fel időközönként random loottal. Manuálisan is triggerelhető `/pvc arena refill`-lel.

---

## Követelmények

- **Paper 1.21.4+** (Spigot is megy, de Paper ajánlott)
- **Java 21**
- **LuckPerms** *(opcionális — permission integráció, prefix megjelenítés statsoknál)*
- **DecentHolograms** *(opcionális — jobb hologram rendering)*

---

## Telepítés

1. Dobd be a `PvPCore.jar`-t a `plugins/` mappába
2. Indítsd el a szervert egyszer hogy legenerálja a configokat
3. Állítsd be a `plugins/PvPCore/` mappában lévő yml fájlokat
4. `/pvc reload` és kész

Semmi más nem kell hozzá.

---

## Config fájlok

Minden modul külön fájlban van, nem egy gigantikus config.yml-ben:

| Fájl | Mit állít |
|---|---|
| `config.yml` | Globális beállítások (language, debug, server-mode) |
| `combattag.yml` | Tag időtartam, kilépés büntetés, üzenetek |
| `killstreak.yml` | Mérföldkövek, broadcast küszöb, hangok, jutalmak |
| `bounty.yml` | Min/max összeg, broadcast üzenetek |
| `kits.yml` | Kit definíciók, cooldownok, itemek |
| `prestige.yml` | Szintek, szükséges ölések, jutalmak, display tagok |
| `economy.yml` | Kill jutalom, szorzók, death-drop százalék |
| `arena.yml` | Chest refill időköz |
| `leaderboard.yml` | Hologram pozíció, frissítési időköz, formátum |

Minden `/pvc reload`-dal újratöltődik, restart nélkül.

---

## Parancsok

| Parancs | Leírás |
|---|---|
| `/kit` | GUI kit választó |
| `/kit <nev>` | Kit közvetlen felszerelése |
| `/stats` | Saját statisztikák |
| `/stats <játékos>` | Másik játékos statjai (admin jog kell) |
| `/killstreak` | Jelenlegi és legjobb sorozat |
| `/leaderboard` | Top 10 játékos ölések szerint |
| `/bounty <játékos>` | Fejdíj megtekintése |
| `/bounty <játékos> <összeg>` | Fejdíj kitűzése |
| `/prestige` | Prestige állapot és progress |
| `/prestige claim` | Prestige igénylése ha teljesítve |
| `/coins` | Saját egyenleg |
| `/coins add\|remove\|set <játékos> <összeg>` | Admin coin kezelés |
| `/pvc reload` | Minden config újratöltése |
| `/pvc holo` | Hologram kézi frissítés |
| `/pvc arena refill` | Ládák kézi feltöltése |
| `/pvc saveall` | Minden adat mentése azonnal |

---

## Permissionok

LuckPermsbe állitsd be amit szeretnél:

```
# Alap játékos rang
pvpcore.kit
pvpcore.killstreak
pvpcore.stats
pvpcore.leaderboard
pvpcore.bounty
pvpcore.bounty.set
pvpcore.prestige
pvpcore.coins

# Moderátor
pvpcore.stats.others

# Admin
pvpcore.admin
pvpcore.coins.admin
```

LuckPerms parancsok:
```
/lp group default permission set pvpcore.kit true
/lp group default permission set pvpcore.stats true
/lp group default permission set pvpcore.leaderboard true
/lp group default permission set pvpcore.bounty true
/lp group default permission set pvpcore.bounty.set true
/lp group default permission set pvpcore.prestige true
/lp group default permission set pvpcore.coins true
/lp group default permission set pvpcore.killstreak true

/lp group mod permission set pvpcore.stats.others true

/lp group admin permission set pvpcore.admin true
/lp group admin permission set pvpcore.coins.admin true
```

---


## Hologram

Szerver indításkor automatikusan felismeri hogy van-e DecentHolograms telepítve.

Ha **van** → DH API-n keresztül kezeli, minden DH formátum működik benne.

Ha **nincs** → saját invisible ArmorStand megoldás, semmi extra dependency nem kell, minden 1.21.4+ Paperen megy.

A pozíciót `leaderboard.yml`-ben állítod be, utána `/pvc holo` és mozog a hologram restart nélkül.

---

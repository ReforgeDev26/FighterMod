# Fighter Mod ⚔️

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-success)](https://www.minecraft.net)
[![Forge](https://img.shields.io/badge/Forge-52.1.3-orange)](https://files.minecraftforge.net)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Modrinth](https://img.shields.io/modrinth/dt/НАЗВАНИЕ-ПРОЕКТА?label=Modrinth&logo=modrinth)](https://modrinth.com/mod/fighter-mod)

## 📋 Описание
Fighter Mod добавляет в игру магические амулеты и оружие, которые усиливают способности игрока. Мод разработан для Minecraft Forge 1.21.1.

## ✨ Особенности

### 🏆 Предметы

| Предмет | Описание | Эффект |
|---------|----------|--------|
| **Критический амулет** | Усиливает урон оружия | ×2 урона, тратит 2 голода за удар |
| **Амулет скорости** | Увеличивает скорость передвижения | Скорость II при движении, тратит 1 голода |
| **Критический меч** | Мощное оружие | Не тратит голод при использовании с амулетом |

### ⚡ Механики

#### 🔮 Критический амулет
- Держите в **левой руке** для активации
- Удваивает урон любого оружия в правой руке
- Тратит **2 голода** за удар
- Имеет **100 прочности**
- Кулдаун **0.5 секунды**
- Создает критический эффект при ударе

#### ⚡ Амулет скорости
- Держите в **левой руке** для активации
- Активируется при **движении** (ходьба/бег/прыжки)
- Дает эффект **Скорость II** на 5 секунд
- Тратит **1 голода** при активации
- Кулдаун **10 секунд** между активациями
- Визуальные эффекты (облака, частицы)

#### ⚔️ Критический меч
- Работает в связке с критическим амулетом
- **Не тратит голод** при активации амулета
- Имеет характеристики, схожие с золотым мечом

## 🎮 Рецепты крафта

### Критический амулет
```
GGG
GRG
GGG
```
G = Золотой слиток (Gold Ingot)
R = Редстоун (Redstone)



### Амулет скорости
```
FFF
FRF
FFF
```
F = Перо (Feather)
R = Редстоун (Redstone)


### Критический меч
```
 I
 I
 S
```
I = Железный слиток (Iron Ingot)
S = Палка (Stick)


## 📥 Установка

### Требования:
- Minecraft **1.21.1**
- Minecraft Forge **52.1.3**

### Инструкция:
1. Скачайте и установите [Minecraft Forge 1.21.1](https://files.minecraftforge.net)
2. Скачайте последнюю версию мода из [релизов](https://github.com/ReforgeDev26/FighterMod/releases)
3. Поместите файл `.jar` в папку `mods` игры
    - Windows: `%appdata%/.minecraft/mods`
    - Linux: `~/.minecraft/mods`
    - Mac: `~/Library/Application Support/minecraft/mods`
4. Запустите игру с профилем Forge

## 🔧 Разработка

### Для разработчиков:

1. **Клонируйте репозиторий:**
```bash
git clone https://github.com/ReforgeDev26/FighterMod/fightermod.git
cd fightermod
```
Настройте среду разработки:

```bash

# Для Eclipse
gradlew eclipse

# Для IntelliJ IDEA
gradlew idea
gradlew genIntellijRuns
```
Соберите проект:

```bash

gradlew build
```
Запустите клиент:

```bash

gradlew runClient
```
🛠️ Технические детали

    MCP (Mod Coder Pack) - используется для маппингов

    Minecraft Forge 52.1.3 - моддинг API

    Java 21 - версия языка

    Gradle 8.12.1 - система сборки

📊 Версии
Версия мода	Версия Minecraft	Состояние
1.0.0	1.21.1	✅ Релиз

📝 Лицензия


Этот проект распространяется под лицензией MIT. Подробнее в файле LICENSE.




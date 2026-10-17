# KubeJS

Мод поддерживает KubeJS без всяких обходных путей: скрипты видят датапак мода, могут
убирать и менять его рецепты и **добавлять рецепты наших машинных типов**.

Проверено на живом сервере: скрипт добавил рецепт `industrialreforged:macerator`
(золотое яблоко → 4 пыли света), и дробилка его отработала.

## Что нужно

- **KubeJS** `2101.7.2-build.377` для NeoForge 1.21.1
- **rhino** `2101.2.8-build.91` — движок JavaScript, без него KubeJS не стартует
  (`Mod kubejs requires rhino 2101.2.7-build.81 or above`)

Оба мода необязательны для нашего: без них мод работает как обычно. Зависимость на
KubeJS в `mods.toml` объявлена как `optional`.

## Куда класть скрипты

Для выделенного сервера — в `<сервер>/kubejs/`:

```
kubejs/
  startup_scripts/     — StartupEvents, выполняется один раз
  server_scripts/      — ServerEvents, рецепты, события (только на сервере)
  client_scripts/      — клиентские события
```

После правки скриптов нужны **две** команды — в KubeJS 7 перечитывание разнесено:

```
/kubejs reload server-scripts   # перечитать СКРИПТЫ
/reload                         # перечитать рецепты, теги и таблицы лута
```

Одной `/kubejs reload server` недостаточно: правильный синтаксис — `server-scripts`,
и сам KubeJS в подсказке пишет «To reload recipes ... run /reload».

## Машинные рецепты

Наши типы рецептов видны скриптам как есть:

| Тип | Что делает | Обязательные поля |
|---|---|---|
| `industrialreforged:macerator` | дробилка | `ingredient`, `result` |
| `industrialreforged:compressor` | компрессор | `ingredient`, `result` |
| `industrialreforged:extractor` | экстрактор | `ingredient`, `result` |
| `industrialreforged:centrifuge` | центрифуга | `ingredient`, `result`, `minHeat` |
| `industrialreforged:metal_former` | металлоформер | `ingredient`, `result`, `mode` |
| `industrialreforged:block_cutter` | блорез | `ingredient`, `result` |
| `industrialreforged:ore_washer` | рудопромывка | `ingredient`, `result` |
| `industrialreforged:blast_furnace` | доменная печь | `ingredient`, `result`, `cells`, `duration` |
| `industrialreforged:molecular_transformer` | молекулярный трансформер | `ingredient`, `result`, `energy` |
| `industrialreforged:scanner` | сканер КР | `ingredient`, `result`, `uu` |

```js
ServerEvents.recipes(event => {
  // свой рецепт дробилки
  event.custom({
    type: 'industrialreforged:macerator',
    ingredient: { item: 'minecraft:golden_apple' },
    result: { id: 'minecraft:glowstone_dust', count: 4 }
  })

  // убрать рецепт мода
  event.remove({ output: 'industrialreforged:circuit' })

  // заменить его своим крафтом
  event.shaped(
    { id: 'industrialreforged:circuit', count: 1 },
    ['PP'],
    { P: 'minecraft:iron_ingot' }
  )
})
```

## Особенности этой версии KubeJS (7.2.x, NeoForge)

Проверено на `2101.7.2-build.377` — API отличается от старых гайдов, и это стоит знать:

- глобал `Server` в серверных скриптах **не определён**, работайте через `ServerEvents`;
- `event.custom(json)` принимает **один аргумент** — идентификатор рецепта передать нельзя,
  он генерируется автоматически;
- у `event.shaped(...)` порядок аргументов: **результат, потом схема, потом ключи**;
- все методы принимают скрытый первым параметром Rhino `Context` — из скриптов он не виден
  и передаётся сам.

Полный рабочий пример — `docs/kubejs/example_server_scripts.js`. Он покрывает и
машинный рецепт нашего типа, и `blast_furnace` с `cells`/`duration`, и замену крафта.

## Что можно и что нельзя менять скриптами

**Можно:** любые рецепты (крафт, плавка, машинные), удаление и замена существующих,
теги предметов через `ServerEvents.tags`? — нет, теги в KubeJS меняются своими файлами в
`kubejs/assets/`.

**Не имеет смысла скриптами:** баланс генераторов и проводов зашит в код
(`IRConfig`, `GTBlocks`) — это константы, а не рецепты, и KubeJS их не видит. Если нужно
подкрутить выработку панелей, это правка конфига мода или кода.

## Проверка в этом проекте

`tools/tests_kubejs.py` — набор, который проверяет интеграцию на живом сервере
и **пропускается (SKIP)**, если KubeJS не установлен:

1. сервер с KubeJS стартует без ошибок;
2. скрипт добавляет рецепт нашего типа и он **реально отрабатывает** в машине;
3. скрипт убирает рецепт мода, и рецепта действительно нет.

Запуск:

```bash
# один раз: положить KubeJS и rhino в run-server/mods/
#   kubejs-neoforge-2101.7.2-build.377.jar
#   rhino-2101.2.8-build.91.jar          <- без rhino KubeJS не стартует
python3 tools/tests_kubejs.py --base http://127.0.0.1:9879
```

Набор сам пишет временный скрипт в `run-server/kubejs/server_scripts/recipes/ir_test.js`,
перечитывает его и **удаляет за собой**, так что руками ничего чистить не нужно.
Без KubeJS набор печатает `SKIP` и возвращает 0.

Полный прогон с установленным KubeJS (20 наборов) проходит целиком — то есть наш мод
не ломается от того, что рецептами управляет сторонний мод.

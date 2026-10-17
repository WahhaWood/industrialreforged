// Пример серверного скрипта KubeJS для IndustrialReforged.
// Проверено на KubeJS 2101.7.2-build.377 (NeoForge, MC 1.21.1).
//
// Кладётся в: <сервер>/kubejs/server_scripts/recipes/example.js
// После правки: /kubejs reload server-scripts, затем /reload
//
// ВАЖНО про API этой версии:
//  * глобал `Server` не определён — только ServerEvents;
//  * event.custom(json) принимает ОДИН аргумент, id рецепта не передаётся;
//  * event.shaped(result, pattern, key) — порядок именно такой.

ServerEvents.recipes(event => {
  // 1. свой рецепт дробилки: золотое яблоко -> 4 пыли света
  event.custom({
    type: 'industrialreforged:macerator',
    ingredient: { item: 'minecraft:golden_apple' },
    result: { id: 'minecraft:glowstone_dust', count: 4 }
  })

  // 2. доменная печь: свой сплав с расходом ячеек и временем
  event.custom({
    type: 'industrialreforged:blast_furnace',
    ingredient: { tag: 'c:ingots/iron' },
    cells: 4,                 // сжатых воздушных ячеек на операцию
    duration: 3000,            // тиков
    result: [
      { id: 'industrialreforged:steel_ingot', count: 1 },
      { id: 'industrialreforged:slag', count: 1 }
    ]
  })

  // 3. убрать рецепт мода и заменить своим
  event.remove({ output: 'industrialreforged:circuit' })
  event.shaped(
    { id: 'industrialreforged:circuit', count: 1 },
    ['PP'],
    { P: 'minecraft:iron_ingot' }
  )
})

// 4. пример события: подсказка игроку в чат, когда он ставит печь
ServerEvents.tick(event => {
  if (event.tickCount === 200) {
    console.info('[example] скрипт загружен, мод на месте: '
      + !!Java.loadClass('com.industrialreforged.IndustrialReforged'))
  }
})

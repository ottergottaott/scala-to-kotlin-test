### dsl для чеклистов
**UPD** Нашел после дедлайна баг с приоритетами арифметических операций, исправил его и подчистил грамматику, залил в отедльную ветку: https://github.com/aleksZubakov/scala-to-kotlin-test/tree/after_deadline

Как это работает
---
- простой регуляркой собирается конфиг файл, в котором лежат объявления глобальных переменных(об этом ниже)
- далее antlr парсер стравливается на файл с шаблоном
- с родным представлением antlr работать достаточно неудобно, поэтому здесь запускается трансформер, который из antlr parsetree собирает самописное дерево
- по построенному дереву проходит несколько визиторов, чтобы проверить на потенциальные ошибки
    - визитор, проверяющий все ли переменные были объявлены до момента использования
    - визитор, проверяющий типы на валидное использование(не сложили строку с числом и т.д.)
    - визитор, сопоставляющий сигнатуру функции с типами аргументов при вызове
- если какой-то из вышеописанных визиторов находит ошибку, об этом сообщается пользователю
- после запускается еще один визитор, который производит все вычисления и параллельно выводит все на экран

Подобие спецификации
----------
- Шаблон обязательно начинается с заголовка: двух символов '##' и какого-то неупустого текста, в тексте допустимы русские буквы и знаки препинная: `[!?|.]`(запятая умерла)
- Индентификаторы состоят только из букв алфавита русского языка
- Вложенность блоков осуществляется пробелами, как в python. Смотрится приятно, пользователю удобно и понятно.
- Переменные можно инлайнить в текст с помощью `placeholder`; Пример: есть переменная `какаяТоПеременная` со значением `значение`, следующая строка
```
слово $какаяТоПерменная слово
``` 
превратится после интерпретации в такую:
```
слово значение слово
``` 
- Placeholder так же может быть сложносочиненным(но только арифметическим выражением);
Например, если есть переменная `какаяТоПеременная` со значением `42`, то следующий код
```
слово ${(какаяТоПеременная + 2 - 2)*1}
```
Превратится в 
```
слово 42.0
```
- Есть условные выражения, в них может участвовать все, что в результате должно дать `bool`:
```
${(пер > 10) || ((чтоТоЕще + 20) < 0.3)}
    строка условного выражения 1
    строка условного выражения 2
    строка условного выражения 3
```
во время работы весь блок выводится на экран, если условия выполнены
- Можно определять свои функции; функция начинается с символов `$$`, аргументов может быть любое количество, они обязательно аннотирвоаны типом(все ведь любят явную типизацию). Функция ничего не возвращает, но инлайнит весь текст при интерпретации. Пример:
```
$$назВаниеФункции(аргумент: число, другойАргумент: стркоа)
    первая строка
    вторая
```
- Вызов функции:
```
функция(20, переменная)
```
можно вызывать, как от сырых числовыъ значений, так и от переменных
- В dsl есть два типа `строка` и `число`
- Можно объявлять свои переменные, синтаксис следующий
```
Какое-то описание : названиеПеременной  : <строка|число> : значение
```

Config файл
---
Представляет собой набор глобальных переменных, должен быть задан в следующем виде:
```
спина: число: 20
ноги: строка
голова: строка : нужна
уши: число:  10
```
т.е. одна строка — одно объявление переменной `<название_без_пробелов> : <тип> : значение`, у строки значение опционально, можно пропустить.


### Как запустить
На вход должны быть поданы `<путь_к_конфиг_файлу> <путь_к_шаблону>`

Пример шаблона и конфига есть в файлах `test.checklist` и `test.config`

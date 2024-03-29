## Спрайты вида сбоку

Для игр с боковым обзором графические элементы 16×16 (вместе с масками), составляющие спрайтсет, должны располагаться в
таком порядке:
|#|Значение
|---|---
|0|Главный герой, справа, ходьба, кадр 1
|1|Главный герой, справа, ходьба, кадр 2, или стоя
|2|Главный герой, справа, ходьба, кадр 3
|3|Главный герой (справа) в воздухе
|4|Главный герой, слева, ходьба, кадр 1
|5|Главный герой, слева, ходьба, кадр 2, или стоя
|6|Главный герой, слева, ходьба, кадр 3
|7|Главный герой (слева) в воздухе
|8|Тип противника 1, кадр 1
|9|Тип противника 1, кадр 2
|10|Тип противника 2, кадр 1
|11|Тип противника 2, кадр 2
|12|Тип противника 3, кадр 1
|13|Тип противника 3, кадр 2
|14|Мобильная платформа, кадр 1
|15|Мобильная платформа, кадр 2

Как мы видим, первые восемь тайлов используются для анимации главного героя: четыре - когда он смотрит вправо, и еще
четыре - когда он смотрит влево.

У нас есть три основные анимации для персонажа: стояние, ходьба и прыжки/падения:

1. **Stopped**: персонаж неподвижен (как следует из названия). Остановленный означает, что он не движется сам по себе (
   если его перемещает внешний объект, он все равно "остановлен"). Когда персонаж неподвижен, движок рисует его,
   используя кадр 2 (#1, если смотрит направо, или #5, если смотрит налево).

2. **Ходьба**: персонаж перемещается по платформе вбок. В этом случае делается четырехшаговая анимация с использованием
   кадров 1, 2, 3, 2,... в таком порядке (графика 0, 1, 2, 1..., если мы смотрим направо, или 4, 5, 6, 5..., если мы
   смотрим налево). При рисовании персонаж должен стоять обеими ногами на земле в кадре 2, а в кадрах 1 и 3 ноги должны
   быть вытянуты (левая или правая впереди). Вот почему мы используем кадр 2 в анимации "стояния".

3. **Прыжок/Падение**: персонаж прыгает или падает (черт, слава богу, я ясно выразился!). Затем движок рисует кадр "
   прыжка" (графический номер 3, если смотреть направо, или номер 7, если смотреть налево).

Для изображения врагов используются следующие шесть графических элементов. Враги могут быть трех типов, и каждый из них
имеет два кадра анимации.

Наконец, два последних спрайта используются для подвижных платформ, которые также имеют два кадра анимации. Подвижные
платформы - это, как следует из названия, платформы, которые двигаются. Главный герой сможет ездить на них, чтобы
передвигаться. Чтобы нарисовать спрайт, мы должны позаботиться о том, чтобы поверхность, на которой должен стоять
главный герой, касалась верхнего края рисунка.

### Para que quede claro, veamos otro ejemplo

![El spriteset de Cherils Perils](https://raw.githubusercontent.com/mojontwins/MK1/master/docs/wiki-img/04_spriteset_perils.png)

Приведенный выше спрайтсет соответствует **Cheril Perils**. Как мы видим, первые восемь спрайтов изображает Шерил,
которая сначала смотрит направо, а затем налево. Затем идут три врага, которых мы видим в игре, и в конце - движущаяся
платформа. Внимательно посмотрите на анимацию ходьбы, представьте, как вы переходите от кадра 1 к 2, от 2 к 3, от 3 к 2
и от 2 к 1. Посмотрите на спрайты и представьте ее в своей голове, вы видите ее? Вы видите, как она двигает лапами?
Пинг, пинг, пинг, пинг, пинг, пинг... Обратите внимание, что кадр 2 лучше всего подходит, когда персонаж стоит на месте.

## Спрайты с видом на сверху-вниз

Для игр с видом сверху 16 спрайтов должны располагаться в таком порядке:

| #  | Значение                              
|----|---------------------------------------
| 0  | Главный герой, справа, ходьба, кадр 1 
| 1  | Главный герой, справа, ходьба, кадр 2 
| 2  | Главный герой, слева, ходьба, кадр 1  
| 3  | Главный герой, слева, ходьба, кадр 2  
| 4  | Главный герой, вверх, ходьба, кадр 1  
| 5  | Главный герой, вверх, ходьба, кадр 2  
| 6  | Главный герой, вниз, ходьба, кадр 1   
| 7  | Главный герой, вниз, ходьба, кадр 2   
| 8  | Тип противника 1, кадр 1              
| 9  | Тип противника 1, кадр 2              
| 10 | Тип противника 2, кадр 1              
| 11 | Тип противника 2, кадр 2              
| 12 | Тип противника 3, кадр 1              
| 13 | Тип противника 3, кадр 2              
| 14 | Тип противника 4, кадр 1              
| 15 | Тип противника 4, кадр 2              

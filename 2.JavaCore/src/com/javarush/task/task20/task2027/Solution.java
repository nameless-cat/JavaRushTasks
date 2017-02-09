package com.javarush.task.task20.task2027;

import java.util.ArrayList;
import java.util.List;

/* 
Кроссворд
*/

/*
проходим массив полностью для каждого слова отдельно (как вариант запустить несколько потоков, каждый со своим словом)
находим точку входа в слово --- начальную или конечную букву
дальше в радиусе одного символа (по горизонтали, вертикали и диагонали) ищем следующую букву по порядку следования в слове (назад или вперед)
если следующая буква найдена, то продолжаем поиск остальных букв в полученном векторе направления
иначе двигаемся по массиву дальше
 */
public class Solution
{
    public static void main(String[] args)
    {
        int[][] crossword = new int[][]{
                {'f', 'd', 'e', 'r', 'l', 'k'},
                {'u', 's', 'a', 'm', 'e', 'o'},
                {'l', 'n', 'g', 'r', 'o', 'v'},
                {'m', 'l', 'p', 'r', 'r', 'h'},
                {'p', 'o', 'e', 'e', 'j', 'j'},
                {'a', 't', 's', 'i', 'l', 's'},
                {'p', 'r', 'y', 't', 'h', 'x'},
                {'y', 'w', 'r', 'z', 'v', 'i'},
                {'q', 'l', 'd', 'a', 'h', 'k'},
                {'r', 'p', 'g', 'f', 'y', 't'}
        };

        long start = System.currentTimeMillis();
        List<Word> words = detectAllWords(crossword, "home", "same", "list", "array");
        long end = System.currentTimeMillis();

        System.out.println("time: " + (end - start) / 1000 + " s");
        System.out.println("words: " + words.size());

        for (Word word : words)
            System.out.println(word);
        /*
Ожидаемый результат
home - (5, 3) - (2, 0)
same - (1, 1) - (4, 1)
list - (4, 5) - (1, 5)
array - (0, 5) - (4, 9)
         */
    }

    public static List<Word> detectAllWords(int[][] crossword, String... words)
    {
        crossField = crossword;
        List<Thread> threads = new ArrayList<>();
        boolean stillRunning;

        for (String word : words)
        {
            Thread thread = new WordFinder(word);
            threads.add(thread);
            thread.start();
        }

        while (true)
        {
            stillRunning = false;

            for (Thread thread : threads)
            {
                if (thread.isAlive())
                {
                    stillRunning = true;
                    break;
                }
            }

            if (stillRunning)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {}
            }
            else
                break;
        }

        return foundedWords;
    }

    private static int[][] crossField;

    private static List<Word> foundedWords = new ArrayList<>();

    public static class WordFinder extends Thread
    {
        private Word word;
        private String asString;

        public WordFinder(String word)
        {
            this.word = new Solution.Word(word);
            this.asString = word;
        }

        private boolean findWord(int x, int y, boolean straight)
        {
            int iFrom = x - 1, iTo = x + 1, jFrom = y - 1, jTo = y + 1;

            if (iFrom < 0)
                iFrom = 0;
            if (jFrom < 0)
                jFrom = 0;
            if (iTo > crossField.length - 1)
                iTo = crossField.length - 1;
            if (jTo > crossField[0].length - 1)
                jTo = crossField[0].length - 1;

            for (int i = iFrom; i <= iTo; i++)
            {
                for (int j = jFrom; j <= jTo; j++)
                {
                    if (i == x && j == y) // координаты первой/конечной буквы
                        continue;

                    if (isWordFounded(x, y, i - x, j - y, straight))
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean isWordFounded(int initI, int initJ, int vectorI, int vectorJ, boolean straight)
        {
            char[] asChar = asString.toCharArray();
            int i = initI, j = initJ;

            for (int k = 1; k < asChar.length; k++)
            {
                int index = straight ? 0 + k : asChar.length - 1 - k;
                i += vectorI;
                j += vectorJ;

                try
                {
                    int t = crossField[i][j];
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    return false;
                }

                if (asChar[index] != (char) crossField[i][j])
                    return false;
            }

            int startX, startY, endX, endY;

            if (straight)
            {
                startX = initJ;
                startY = initI;
                endX = j;
                endY = i;
            }
            else
            {
                startX = j;
                startY = i;
                endX = initJ;
                endY = initI;
            }

            word.setStartPoint(startX, startY);
            word.setEndPoint(endX, endY);

            return true;
        }

        @Override
        public void run()
        {
            char first = asString.charAt(0);
            char last = asString.charAt(asString.length() - 1);
            boolean straight;

            for (int i = 0; i < crossField.length; i++)
            {
                for (int j = 0; j < crossField[0].length; j++)
                {
                    char nextChar = (char) crossField[i][j];
                    straight = false;

                    if (nextChar == first || nextChar == last)
                    {
                        if (nextChar == first)
                            straight = true;

                        if (!findWord(i, j, straight))
                            continue;
                        else
                        {
                            synchronized (Solution.class)
                            {
                                foundedWords.add(word);
                            }
                            return;
                        }
                    }
                }

            }
        }

        public Word getWord()
        {
            return this.word;
        }
    }

    public static class Word
    {
        private String text;
        private int startX;
        private int startY;
        private int endX;
        private int endY;

        public Word(String text)
        {
            this.text = text;
        }

        public void setStartPoint(int i, int j)
        {
            startX = i;
            startY = j;
        }

        public void setEndPoint(int i, int j)
        {
            endX = i;
            endY = j;
        }

        @Override
        public String toString()
        {
            return String.format("%s - (%d, %d) - (%d, %d)", text, startX, startY, endX, endY);
        }
    }
}

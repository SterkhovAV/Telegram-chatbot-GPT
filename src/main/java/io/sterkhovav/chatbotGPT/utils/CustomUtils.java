package io.sterkhovav.chatbotGPT.utils;

public class CustomUtils {

    public static String escapeMarkdown(String text) {
        StringBuilder result = new StringBuilder();
        boolean inCodeBlock = false;
        boolean inBoldText = false;
        boolean inInlineCode = false;
        int i = 0;

        while (i < text.length()) {
            if (i + 2 < text.length() && text.startsWith("```", i)) {
                inCodeBlock = !inCodeBlock;
                result.append("```");
                i += 3;
            } else if (!inCodeBlock && i + 1 < text.length() && text.startsWith("**", i)) {
                inBoldText = !inBoldText;
                result.append("**");
                i += 2;
            } else if (!inCodeBlock && !inBoldText && text.charAt(i) == '`') {
                inInlineCode = !inInlineCode;
                result.append('`');
                i++;
            } else if (!inCodeBlock && !inBoldText && !inInlineCode) {
                char c = text.charAt(i);
                switch (c) {
                    case '_': case '*': case '[': case ']': case '(': case ')':
                    case '~': case '`': case '>': case '#': case '+': case '-':
                    case '=': case '|': case '{': case '}': case '.': case '!':
                        result.append('\\');
                        result.append(c);
                        break;
                    default:
                        result.append(c);
                }
                i++;
            } else {
                result.append(text.charAt(i));
                i++;
            }
        }

        return result.toString();
    }
}

package cc.funkemunky.api.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Deprecated
public class CommandMessages {
    private String noPermission, invalidArguments, playerOnly, consoleOnly, primaryColor, secondaryColor, titleColor, errorColor, valueColor, successColor;
}

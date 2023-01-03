package wordtool.scanner;

import java.io.File;

public class ScanConfig {
    final File file;
    final boolean useForStart;
    final boolean useForMid;
    final boolean useForEnding;
    final String exprAndFlags;
    
    public ScanConfig(
        final File file,
        final boolean useForStart,
        final boolean useForMid,
        final boolean useForEnding) {
        
        this(file, useForStart, useForMid, useForEnding, null);
    }
    
    public ScanConfig(
        final File file,
        final boolean useForStart,
        final boolean useForMid,
        final boolean useForEnding,
        final String exprAndFlags) {
        
        this.file = file;
        this.useForEnding = useForEnding;
        this.useForMid = useForMid;
        this.useForStart = useForStart;
        this.exprAndFlags = exprAndFlags;
    }
    
    public File getFile() {
        return file;
    }
    
    public boolean isUseForStart() {
        return useForStart;
    }
    
    public boolean isUseForMid() {
        return useForMid;
    }
    
    public boolean isUseForEnding() {
        return useForEnding;
    }
    
    public String getExprAndFlags() {
        return exprAndFlags;
    }
}

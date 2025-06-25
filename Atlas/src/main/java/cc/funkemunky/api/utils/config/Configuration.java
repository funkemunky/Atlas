package cc.funkemunky.api.utils.config;

import java.util.*;

public final class Configuration
{

    private static final char SEPARATOR = '.';
    public final Map<String, Object> self;
    final Map<String, List<String>> comments;
    private final Configuration defaults;

    public Configuration()
    {
        this( null );
    }

    public Configuration(Configuration defaults)
    {
        this( new LinkedHashMap<String, Object>(), defaults );
    }

    Configuration(Map<?, ?> map, Configuration defaults)
    {
        this.self = new LinkedHashMap<>();
        this.defaults = defaults;
        comments = new HashMap<>();

        if(map != null)
        for ( Map.Entry<?, ?> entry : map.entrySet() )
        {
            String key = ( entry.getKey() == null ) ? "null" : entry.getKey().toString();

            if ( entry.getValue() instanceof Map )
            {
                this.self.put( key, new Configuration( (Map) entry.getValue(), ( defaults == null ) ? null : defaults.getSection( key ) ) );
            } else
            {
                this.self.put( key, entry.getValue() );
            }
        }
    }

    public void loadFromString(String contents) {

        List<String> list = new ArrayList<>();
        Collections.addAll(list, contents.split("\n"));

        int currentLayer = 0;
        String currentPath = "";

        int lineNumber = 0;
        for(Iterator<String> iterator = list.iterator(); iterator.hasNext(); lineNumber++) {
            String line = iterator.next();

            String trimmed = line.trim();
            if(trimmed.startsWith("#") || trimmed.isEmpty()) {
                addCommentLine(currentPath, line);
                continue;
            }

            if(!line.isEmpty()) {
                if(line.contains(":")) {

                    int layerFromLine = getLayerFromLine(line, lineNumber);

                    if(layerFromLine < currentLayer) {
                        currentPath = regressPathBy(currentLayer - layerFromLine, currentPath);
                    }

                    String key = getKeyFromLine(line);

                    if(currentLayer == 0) {
                        currentPath = key;
                    }
                    else {
                        currentPath += "." + key;
                    }
                }
            }
        }
    }

    private void addCommentLine(String currentPath, String line) {

        List<String> list = comments.get(currentPath);
        if(list == null) {
            list = new ArrayList<>();
        }
        list.add(line);

        comments.put(currentPath, list);
    }

    String getKeyFromLine(String line) {
        String key = null;

        for(int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == ':') {
                key = line.substring(0, i);
                break;
            }
        }

        return key == null ? null : key.trim();
    }

    String regressPathBy(int i, String currentPath) {
        if(i <= 0) {
            return currentPath;
        }
        String[] split = currentPath.split("\\.");

        String rebuild = "";
        for(int j = 0; j < split.length - i; j++) {
            rebuild += split[j];
            if(j <= (split.length - j)) {
                rebuild += ".";
            }
        }

        return rebuild;
    }

    int getLayerFromLine(String line, int lineNumber) {

        double d = 0;
        for(int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == ' ') {
                d += 0.5;
            }
            else {
                break;
            }
        }

        return (int) d;

    }

    private Configuration getSectionFor(String path)
    {
        int index = path.indexOf( SEPARATOR );
        if ( index == -1 )
        {
            return this;
        }

        String root = path.substring( 0, index );
        Object section = self.get( root );
        if ( section == null )
        {
            section = new Configuration( ( defaults == null ) ? null : defaults.getSection( root ) );
            self.put( root, section );
        }

        return (Configuration) section;
    }

    private String getChild(String path)
    {
        int index = path.indexOf( SEPARATOR );
        return ( index == -1 ) ? path : path.substring( index + 1 );
    }

    /*------------------------------------------------------------------------*/
    @SuppressWarnings("unchecked")
    public <T> T get(String path, T def)
    {
        Configuration section = getSectionFor( path );
        Object val;
        if ( section == this )
        {
            var = self.get( path );
        } else
        {
            var = section.get( getChild( path ), def );
        }

        if ( var == null && def instanceof Configuration )
        {
            self.put( path, def );
        }

        return ( var != null ) ? (T) var : def;
    }

    public boolean contains(String path)
    {
        return get( path, null ) != null;
    }

    public Object get(String path)
    {
        return get( path, getDefault( path ) );
    }

    public Object getDefault(String path)
    {
        return ( defaults == null ) ? null : defaults.get( path );
    }

    public void set(String path, Object value)
    {
        if ( value instanceof Map )
        {
            value = new Configuration( (Map) value, ( defaults == null ) ? null : defaults.getSection( path ) );
        }

        Configuration section = getSectionFor( path );
        if ( section == this )
        {
            if ( value == null )
            {
                self.remove( path );
            } else
            {
                self.put( path, value );
            }
        } else
        {
            section.set( getChild( path ), value );
        }
    }

    /*------------------------------------------------------------------------*/
    public Configuration getSection(String path)
    {
        Object def = getDefault( path );
        return (Configuration) get( path, ( def instanceof Configuration ) ? def : new Configuration( ( defaults == null ) ? null : defaults.getSection( path ) ) );
    }

    /**
     * Gets keys, not deep by default.
     *
     * @return top level keys for this section
     */
    public Collection<String> getKeys()
    {
        return new LinkedHashSet<>( self.keySet() );
    }

    /*------------------------------------------------------------------------*/
    public byte getByte(String path)
    {
        Object def = getDefault( path );
        return getByte( path, ( def instanceof Number ) ? ( (Number) def ).byteValue() : 0 );
    }

    public byte getByte(String path, byte def)
    {
        Object var = get( path, def );
        return ( var instanceof Number ) ? ( (Number) var ).byteValue() : def;
    }

    public List<Byte> getByteList(String path)
    {
        List<?> list = getList( path );
        List<Byte> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).byteValue() );
            }
        }

        return result;
    }

    public short getShort(String path)
    {
        Object def = getDefault( path );
        return getShort( path, ( def instanceof Number ) ? ( (Number) def ).shortValue() : 0 );
    }

    public short getShort(String path, short def)
    {
        Object var = get( path, def );
        return ( var instanceof Number ) ? ( (Number) var ).shortValue() : def;
    }

    public List<Short> getShortList(String path)
    {
        List<?> list = getList( path );
        List<Short> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).shortValue() );
            }
        }

        return result;
    }

    public int getInt(String path)
    {
        Object def = getDefault( path );
        return getInt( path, ( def instanceof Number ) ? ( (Number) def ).intValue() : 0 );
    }

    public int getInt(String path, int def)
    {
        Object var = get( path, def );
        return ( var instanceof Number ) ? ( (Number) var ).intValue() : def;
    }

    public List<Integer> getIntList(String path)
    {
        List<?> list = getList( path );
        List<Integer> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).intValue() );
            }
        }

        return result;
    }

    public long getLong(String path)
    {
        Object def = getDefault( path );
        return getLong( path, ( def instanceof Number ) ? ( (Number) def ).longValue() : 0 );
    }

    public long getLong(String path, long def)
    {
        Object var = get( path, def );
        return ( var instanceof Number ) ? ( (Number) var ).longValue() : def;
    }

    public List<Long> getLongList(String path)
    {
        List<?> list = getList( path );
        List<Long> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).longValue() );
            }
        }

        return result;
    }

    public float getFloat(String path)
    {
        Object def = getDefault( path );
        return getFloat( path, ( def instanceof Number ) ? ( (Number) def ).floatValue() : 0 );
    }

    public float getFloat(String path, float def)
    {
        Object var = get( path, def );
        return ( var instanceof Number ) ? ( (Number) var ).floatValue() : def;
    }

    public List<Float> getFloatList(String path)
    {
        List<?> list = getList( path );
        List<Float> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).floatValue() );
            }
        }

        return result;
    }

    public double getDouble(String path)
    {
        Object def = getDefault( path );
        return getDouble( path, ( def instanceof Number ) ? ( (Number) def ).doubleValue() : 0 );
    }

    public double getDouble(String path, double def)
    {
        Object var = get( path, def );
        return ( var instanceof Number ) ? ( (Number) var ).doubleValue() : def;
    }

    public List<Double> getDoubleList(String path)
    {
        List<?> list = getList( path );
        List<Double> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).doubleValue() );
            }
        }

        return result;
    }

    public boolean getBoolean(String path)
    {
        Object def = getDefault( path );
        return getBoolean( path, ( def instanceof Boolean ) ? (Boolean) def : false );
    }

    public boolean getBoolean(String path, boolean def)
    {
        Object var = get( path, def );
        return ( var instanceof Boolean ) ? (Boolean) var : def;
    }

    public List<Boolean> getBooleanList(String path)
    {
        List<?> list = getList( path );
        List<Boolean> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Boolean )
            {
                result.add( (Boolean) object );
            }
        }

        return result;
    }

    public char getChar(String path)
    {
        Object def = getDefault( path );
        return getChar( path, ( def instanceof Character ) ? (Character) def : '\u0000' );
    }

    public char getChar(String path, char def)
    {
        Object var = get( path, def );
        return ( var instanceof Character ) ? (Character) var : def;
    }

    public List<Character> getCharList(String path)
    {
        List<?> list = getList( path );
        List<Character> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Character )
            {
                result.add( (Character) object );
            }
        }

        return result;
    }

    public String getString(String path)
    {
        Object def = getDefault( path );
        return getString( path, ( def instanceof String ) ? (String) def : "" );
    }

    public String getString(String path, String def)
    {
        Object var = get( path, def );
        return ( var instanceof String ) ? (String) var : def;
    }

    public List<String> getStringList(String path)
    {
        List<?> list = getList( path );
        List<String> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof String )
            {
                result.add( (String) object );
            }
        }

        return result;
    }

    /*------------------------------------------------------------------------*/
    public List<?> getList(String path)
    {
        Object def = getDefault( path );
        return getList( path, ( def instanceof List<?> ) ? (List<?>) def : Collections.EMPTY_LIST );
    }

    public List<?> getList(String path, List<?> def)
    {
        Object var = get( path, def );
        return ( var instanceof List<?> ) ? (List<?>) var : def;
    }
}
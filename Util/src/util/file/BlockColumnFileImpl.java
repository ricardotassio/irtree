package util.file;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import util.statistics.StatisticCenter;


/**
 * Um arquivo de blocos e'  um array de blocos de
 * bytes distrubuidos em varios arquivos fisicos.
 *
 * Cada arquivo possui dois parametros de meta informacao
 * que sao:
 * 1. O tamanho de cada bloco em bytes
 * 2. O numero de blocos que cada arquivo fisico comporta
 *
 * Cada bloco no arquivo e'  identificado por um inteiro
 * que vai de 1 ate no numero de blocos do arquivo.
 *
 * O Arquivo de blocos possui funcoes para:
 * 1. ler um bloco dado o id
 * 2. escrever um bloco de id dado
 * 3. alterar o tamanho do arquivo(truncar arquivo)
 * 4. outras funcoes auxiliares
 *
 */
public class BlockColumnFileImpl implements BlockColumnFile {

    /**
     * tamanho de cada bloco
     */
    private int blockSize;

    /**
     * numerode blocos por arquivo
     */
    private int blocksPerFile;

    /**
     * numero de blocos no arquivo
     */
    private int blockCount;

    /**
     * prefixo do arquivo
     */
    private File prefixfile;

    /**
     * stream de leitura e escrita
     */
    private RandomAccessFile raf;

    /**
     * numero do arquivo aberto
     */
    private int raf_filenum = -1;

    /**
     * variaveis de log
     */
    private long select_calls = 0;
    private long select_time  = 0;
    private long select_max_time = 0;
    private long insert_calls = 0;
    private long insert_time  = 0;
    private long insert_max_time = 0;

    protected StatisticCenter statisticCenter;
    protected String statsId;

    /**
     * Contrutor
     */
    public BlockColumnFileImpl(String file, int blockSize, int blocksPerFile) throws ColumnFileException{
        this(new File(file), blockSize, blocksPerFile);
    }

    /**
     * Contrutor
     */
    public BlockColumnFileImpl(File prefixfile, int blockSize, int blocksPerFile) throws ColumnFileException {
        this.prefixfile = prefixfile;
        this.blockSize = blockSize;
        this.blocksPerFile = blocksPerFile;

        if (blockSize <= 0) {
            throw new ColumnFileException("invalid block size : " + blockSize);
        }

        if (blocksPerFile <= 0) {
            throw new ColumnFileException("invalid number of blocks per file : " + blocksPerFile);
        }

        readBlockCount();
    }

    public void setStatisticCenter(StatisticCenter statisticCenter, String statisticsIdentification){
        this.statisticCenter = statisticCenter;
        this.statsId = statisticsIdentification;
    }

    /**
     * retorna o nome do arquivo para o numero de arquivo dado
     */
    public String getFilename(int filenumber) {
        if (filenumber == 1)
            return prefixfile.toString();
        else
            return prefixfile + "." + filenumber;
    }

    /**
     * le o numero de blocos total
     */
    private void readBlockCount() throws ColumnFileException {
        blockCount = 0;
        int filenum = 1;
        while(true) {
            File file = new File(getFilename(filenum));            
            if (!file.exists()) {
                break;
            }
            blockCount += file.length()/blockSize;
            filenum++;
        }
//        System.out.println("BucketColumnFile> loaded : file prefix="+prefixfile+" blockSize="+blockSize+" blocksPerFile="+blocksPerFile+" blockCount="+blockCount+" fileCount="+(filenum-1));
    }

    /**
     * retorna o prefixo do arquivo
     */
    public File getPrefixFile() {
        return this.prefixfile;
    }

    /**
     * retorna o numero do arquivo do blockid dado
     */
    public int getFileNumber(int blockId) {
        return ((blockId-1)/blocksPerFile) + 1;
    }

    /**
     * retorna o id relativo no arquivo para o blockid dado
     */
    public int getFileRelativeId(int blockId) {
        return ((blockId-1) % blocksPerFile) + 1;
    }

    /**
     * retorna a posicao do bloco no arquivo em numero de bytes
     */
    public long getFilePosition(int blockid) {
        return (long) ((blockid-1)%blocksPerFile)*blockSize;
    }

    /**
     * Load the RandomAccessFile to the blockid given
     */
    private void loadRaf(int blockId) throws ColumnFileException {
        int filenum = getFileNumber(blockId);
        if (filenum == this.raf_filenum) {
            // raf ja esta aberto
            return;
        }
        String filename = getFilename(filenum);
        try {
            if (raf != null) {
                raf.close();
                raf = null;
                filenum = -1;
            }
            this.raf  = new RandomAccessFile(filename, "rw");
            this.raf_filenum = filenum;
        } catch(IOException ioe) {
            throw new ColumnFileException("Erro IO abrindo arquivo " + filename +" ", ioe);
        }
    }

    /**
     * le o block de com o id dado
     */
    public void select(int blockId, byte[] buffer) throws ColumnFileException, DataNotFoundException {
        long time = System.currentTimeMillis();

        if (blockId < 0) {
            throw new ColumnFileException("blockid must be greater than zero");
        }
        if (blockId > size()) {
            throw new DataNotFoundException("block not found: " + blockId);
        }
        if (buffer.length < blockSize) {
            throw new ColumnFileException("buffer length lower than expected : " + blockSize);
        }

        internalSelect(blockId, buffer);

        time = System.currentTimeMillis() - time;
        select_calls++;
        select_time+= time;
        select_max_time=Math.max(select_max_time, time);
    }

    protected void internalSelect(int blockId, byte[] buffer) throws ColumnFileException{
        loadRaf(blockId);
        try {
            long seekpos = getFilePosition(blockId);
//            System.out.println("select id="+blockId+" pos="+seekpos);
            raf.seek(seekpos);
            raf.readFully(buffer, 0, blockSize);
        } catch(IOException ioe) {
            throw new ColumnFileException("Erro IO lendo arquivo blockid="+blockId, ioe);
        }

        if(statisticCenter!=null){
            statisticCenter.getCount(statsId+"_blocksRead").inc();
        }
    }

    /**
     * insere o bloco no arquivo de blockid dado no arquivo
     * se o blockid for maior que o numero de blocos do arquivo
     * entao aumenta o arquivo inserindo blocos zerados(com bytes 0)
     */
    public void insert(int blockid, byte[] buffer) throws ColumnFileException {
        long time = System.currentTimeMillis();
        if (blockid <= 0) {
            throw new ColumnFileException("blockid must be greater than zero");
        }
        if (buffer.length < blockSize) {
            throw new ColumnFileException("buffer length lower than expected : " + blockSize);
        }
        if (blockid - 1 > size()) {
            setSize(blockid - 1);
        }
        if (blockid - 1 == size()) {
            // inserindo no final
            blockCount++;
        }

        internalInsert(blockid, buffer);

        time = System.currentTimeMillis() - time;
        insert_calls++;
        insert_time+= time;
        insert_max_time=Math.max(insert_max_time, time);
    }

    protected void internalInsert(int blockid, byte[] buffer) throws ColumnFileException{
        loadRaf(blockid);
        try {
            long seekpos = getFilePosition(blockid);
//            System.out.println("insert id="+blockid+" pos="+seekpos);
            raf.seek(seekpos);
            raf.write(buffer, 0, blockSize);
        } catch(IOException ioe) {
            throw new ColumnFileException("Erro IO", ioe);
        }

        if(statisticCenter!=null){
            statisticCenter.getCount(statsId+"_blocksWritten").inc();
        }
    }

    /**
     * Return the number of blocks stored persistently in the file
     */
    public int size() throws ColumnFileException{
        return blockCount;
    }

    /**
     * retorna o tamanho do bloco
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * retorna o numero de blocos por arquivo
     */
    public int getBlocksPerFile() {
        return blocksPerFile;
    }

    /**
     * Modifica o numero de blocos do arquivo, se o parametro
     * dado for maior que o numero atual, aumenta o arquivo colocando
     * blocos com bytes 0
     * se o numero for menor, trunca o arquivo
     */
    public void setSize(int size) throws ColumnFileException {
        if (size > size()) {
            // incrementa o arquivo
            byte[] buffer_aux = new byte[blockSize];
            java.util.Arrays.fill(buffer_aux, (byte) 0);
            while(size() < size) {
                insert(size()+1, buffer_aux);
            }
        }

        if (size < size()) {
            // trunca arquivo
            if (size > 0) {
                int filenum = getFileNumber(size);
                long filelen = getFilePosition(size) + blockSize;

                //truca arquivo
                loadRaf(size);
                try {
                    raf.setLength(filelen);
                } catch(IOException ioe) {
                    throw new ColumnFileException("Erro de IO truncando arquivo ", ioe);
                }

                // apaga arquivos nao usados
                for(int i = getFileNumber(size());i>filenum;i--) {
                    File f = new File(getFilename(i));
                    if (!f.delete()) {
                        throw new ColumnFileException("nao conseguiu apagar arquivo " + f);
                    }
                }
                blockCount = size;
            } else {
                // zera arquivo
                close();
                int filenum = 0;
                // apaga arquivos nao usados
                for(int i = getFileNumber(size());i>filenum;i--) {
                    File f = new File(getFilename(i));
                    if (!f.delete()) {
                        throw new ColumnFileException("nao conseguiu apagar arquivo " + f);
                    }
                }
                blockCount = 0;
            }
        }
    }

    /**
     * fecha o arquivo
     */
    public void close() throws ColumnFileException {
        if (raf != null) {
            try {
                raf.close();
                raf = null;
            } catch(IOException ioe) {
                throw new ColumnFileException("Erro IO", ioe);
            }
        }
        this.raf_filenum = -1;
    }

    /**
     * dados em string
     */
    @Override
    public String toString() {
        return "BlockColumnFile [prefix="+getPrefixFile()+
               ",blockSize="+getBlockSize()+
               ",blocksPerFile="+getBlocksPerFile()+
               ",size="+blockCount+
               ",select[calls/avg/max]="+select_calls+","+(select_calls>0L?select_time/select_calls:0L)+","+select_max_time+
               ",insert[calls/avg/max]="+insert_calls+","+(insert_calls>0L?insert_time/insert_calls:0L)+","+insert_max_time
               ;
    }

    public static void main(String[] args) {
       try {
            System.out.println("args[0] = prefix file");
            System.out.println("args[1] = size of each block");
            System.out.println("args[2] = number of blocks per file");
            BlockColumnFile cfLong = new BlockColumnFileImpl(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            // realizacao de pedidos
            java.io.BufferedReader bin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            String input;
            int line;
            byte[] valueBytes;
            byte[] buffer = new byte[cfLong.getBlockSize()];
            int pos;
            System.out.println("Press Enter!");
            while( (input = bin.readLine()) != null ) {
                if( input.equalsIgnoreCase("quit") ) {
                    break;
                }
                else if( input.equalsIgnoreCase("size") ) {
                    System.out.println("SIZE>"+cfLong.size());
                }
                else if( input.equalsIgnoreCase("list") ) {
                    System.out.println("File:"+cfLong.getPrefixFile());
                    for(int i=1;i<=cfLong.size();i++) {
                        cfLong.select(i, buffer);
                        System.out.println("id="+i+" buffer="+new String(buffer));
                    }
                    System.out.println("---");
                }
                else if( input.startsWith("i ") ) {
                    try {
                        pos = input.indexOf(" ",3);
                        line = Integer.parseInt(input.substring(2,pos).trim());
                        valueBytes = input.substring(pos+1).trim().getBytes();
                        java.util.Arrays.fill(buffer, (byte) 0);
                        System.arraycopy(valueBytes, 0, buffer, 0, Math.min(buffer.length, valueBytes.length));
                        System.out.println("INSERT> " + input);
                        cfLong.insert(line,buffer);
                    }
                    catch( Exception exc ) {
                        exc.printStackTrace();
                    }
                }
                else if( input.startsWith("s ") ) {
                    try {
                        line = Integer.parseInt(input.substring(2).trim());
                        cfLong.select(line, buffer);
                        System.out.println("SELECT> id="+line+" : "+new String(buffer));
                    }
                    catch( Exception exc ) {
                        exc.printStackTrace();
                    }
                }
                else if( input.startsWith("l ") ) {
                    try {
                        line = Integer.parseInt(input.substring(2).trim());
                        cfLong.setSize(line);
                        System.out.println("SET_SIZE> "+line);
                    }
                    catch( Exception exc ) {
                        exc.printStackTrace();
                    }
                }
                else {
                    System.out.println("MENU:");
                    System.out.println("quit: Sair");
                    System.out.println("list : Lista o arquivo completo");
                    System.out.println("size : O numero de colunas existentes");
                    System.out.println("Comandos:");
                    System.out.println("i <id> <texto>: Realiza um insert");
                    System.out.println("s <id>: Realiza um select");
                    System.out.println("l <size>: Modifica o tamanho");
                }
                System.out.print(">");
            }
            cfLong.close();
        }
        catch( Exception exc ) {
            exc.printStackTrace();
        }
    }
}
package util.file;

/**
 * Title:        Projeto Radix
 * Description:  Classes utilitarias de todo o Radix.
 * Copyright:    Copyright (c) 1997 - 2000
 * Company:      Radix
 * @author Radix Development Team
 * @version 1.0
 */

import java.io.File;
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
public interface BlockColumnFile {

    /**
     * retorna o nome do arquivo para o numero de arquivo dado
     */
    public String getFilename(int filenumber);

    /**
     * retorna o prefixo do arquivo
     */
    public File getPrefixFile();

    /**
     * retorna o numero do arquivo do blockid dado
     */
    public int getFileNumber(int blockId) ;

    /**
     * retorna o id relativo no arquivo para o blockid dado
     */
    public int getFileRelativeId(int blockId);

    /**
     * retorna a posicao do bloco no arquivo em numero de bytes
     */
    public long getFilePosition(int blockid);

    /**
     * le o block de com o id dado
     */
    public void select(int blockId, byte[] buffer) throws ColumnFileException, DataNotFoundException;

    /**
     * insere o bloco no arquivo de blockid dado no arquivo
     * se o blockid for maior que o numero de blocos do arquivo
     * entao aumenta o arquivo inserindo blocos zerados(com bytes 0)
     */
    public void insert(int blockid, byte[] buffer) throws ColumnFileException;

    /**
     * Sets a statistic center to collect data about write/read operations of the blockColumnFile
     * @param statisticCenter
     * @param statisticsIdentification the statics will followed by the given identification
     */
    public void setStatisticCenter(StatisticCenter statisticCenter, String statisticsIdentification);

    /**
     * retorna o numero de blocos
     */
    public int size() throws ColumnFileException;

    /**
     * retorna o tamanho do bloco
     */
    public int getBlockSize();

    /**
     * retorna o numero de blocos por arquivo
     */
    public int getBlocksPerFile();

    /**
     * Modifica o numero de blocos do arquivo, se o parametro
     * dado for maior que o numero atual, aumenta o arquivo colocando
     * blocos com bytes 0
     * se o numero for menor, trunca o arquivo
     */
    public void setSize(int size) throws ColumnFileException;

    /**
     * fecha o arquivo
     */
    public void close() throws ColumnFileException;

}
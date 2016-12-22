package com.kyj.fx.voeditor.visual.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


public class Base64Utils
{
    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is identified by an optional byte-order mark
     */
    public static final Charset    UTF_16        = Charset.forName( "UTF-16" );
    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is big-endian
     */
    public static final Charset    UTF_16BE    = Charset.forName( "UTF-16BE" );
    /**
     * 16-bit UTF (UCS Transformation Format) whose byte order is little-endian
     */
    public static final Charset    UTF_16LE    = Charset.forName( "UTF-16LE" );
    /**
     * 8-bit UTF (UCS Transformation Format)
     */
    public static final Charset    UTF_8        = Charset.forName( "UTF-8" );
    /**
     * ISO Latin Alphabet No. 1, as known as <tt>ISO-LATIN-1</tt>
     */
    public static final Charset    ISO_8859_1    = Charset.forName( "ISO-8859-1" );
    /**
     * 7-bit ASCII, as known as ISO646-US or the Basic Latin block of the Unicode character set
     */
    public static final Charset    US_ASCII    = Charset.forName( "US-ASCII" );
    private static final char[]    charTab        = { 'A' , 'B' , 'C' , 'D' , 'E' , 'F' , 'G' , 'H' , 'I' , 'J' , 'K' , 'L' , 'M' , 'N' , 'O' , 'P' , 'Q' , 'R' ,
            'S' , 'T' , 'U' , 'V' , 'W' , 'X' , 'Y' , 'Z' , 'a' , 'b' , 'c' , 'd' , 'e' , 'f' , 'g' , 'h' , 'i' , 'j' , 'k' , 'l' , 'm' , 'n' , 'o' , 'p' ,
            'q' , 'r' , 's' , 't' , 'u' , 'v' , 'w' , 'x' , 'y' , 'z' , '0' , '1' , '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , '+' , '/' };


    public static String encode( byte[] b )
    {
        return encode( b , 0 , b.length , null ).toString( );
    }


    private static StringBuilder encode( byte[] b , int i0 , int i1 , StringBuilder sb )
    {
        if ( sb == null )
        {
            sb = new StringBuilder( i1 * 3 / 2 );
        }
        int i = i1 - 3;
        int j = i0;
        int k = 0;
        int m;
        while ( j <= i )
        {
            m = ( b[ j ] & 0xFF ) << 16 | ( b[ ( j + 1 ) ] & 0xFF ) << 8 | b[ ( j + 2 ) ] & 0xFF;
            sb.append( charTab[ ( m >> 18 & 0x3F ) ] );
            sb.append( charTab[ ( m >> 12 & 0x3F ) ] );
            sb.append( charTab[ ( m >> 6 & 0x3F ) ] );
            sb.append( charTab[ ( m & 0x3F ) ] );
            j += 3;
            if ( k++ < 14 )
            {
                continue;
            }
            k = 0;
            // sb.append( "\r\n" );
        }
        if ( j == i0 + i1 - 2 )
        {
            m = ( b[ j ] & 0xFF ) << 16 | ( b[ ( j + 1 ) ] & 0xFF ) << 8;
            sb.append( charTab[ ( m >> 18 & 0x3F ) ] );
            sb.append( charTab[ ( m >> 12 & 0x3F ) ] );
            sb.append( charTab[ ( m >> 6 & 0x3F ) ] );
            sb.append( "=" );
        }
        else if ( j == i0 + i1 - 1 )
        {
            m = ( b[ j ] & 0xFF ) << 16;
            sb.append( charTab[ ( m >> 18 & 0x3F ) ] );
            sb.append( charTab[ ( m >> 12 & 0x3F ) ] );
            sb.append( "==" );
        }
        return sb;
    }


    private static int decode( char c )
    {
        if ( ( c >= 'A' ) && ( c <= 'Z' ) )
        {
            return c - 'A';
        }
        if ( ( c >= 'a' ) && ( c <= 'z' ) )
        {
            return c - 'a' + 26;
        }
        if ( ( c >= '0' ) && ( c <= '9' ) )
        {
            return c - '0' + 26 + 26;
        }
        switch ( c )
        {
            case '+' :
                return 62;
            case '/' :
                return 63;
            case '=' :
                return 0;
        }
        throw new RuntimeException( "unexpected code: " + c );
    }


    public static byte[] decode( String s )
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream( );
        try
        {
            decode( s , baos );
            return baos.toByteArray( );
        }
        catch ( IOException ie )
        {
            throw new RuntimeException( );
        }
        finally
        {
            if ( baos != null )
            {
                try
                {
                    baos.close( );
                }
                catch ( IOException e )
                {}
            }
        }
    }


    private static void decode( String s , OutputStream os ) throws IOException
    {
        int i = 0;
        int j = s.length( );
        while ( true )
        {
            if ( ( i < j ) && ( s.charAt( i ) <= ' ' ) )
            {
                i++ ;
                continue;
            }
            if ( i == j )
            {
                break;
            }
            int k = ( decode( s.charAt( i ) ) << 18 ) + ( decode( s.charAt( i + 1 ) ) << 12 ) + ( decode( s.charAt( i + 2 ) ) << 6 )
                    + decode( s.charAt( i + 3 ) );
            os.write( k >> 16 & 0xFF );
            if ( s.charAt( i + 2 ) == '=' )
            {
                break;
            }
            os.write( k >> 8 & 0xFF );
            if ( s.charAt( i + 3 ) == '=' )
            {
                break;
            }
            os.write( k & 0xFF );
            i += 4;
        }
    }


    public static void main( String[] args ) throws UnsupportedEncodingException
    {

        String namo =
            "<HTML>"+
            "<HEAD>"+
            "<META content='text/html; charset=utf-8' http-equiv=Content-Type>"+
            "<META name=GENERATOR content=ActiveSquare></HEAD>"+
            "<BODY>"+
            "<BR>"+
            "테스트"+
            "<P>"+
            "<IMG style='WIDTH: 1300px; HEIGHT: 847px' src='cid:QKNMBDIFBEI0@namo.co.kr'></P></BODY>"+
            "</HTML>";


        String encode = Base64Utils.encode( namo.getBytes( ) );
        System.out.println(encode);
    }


}



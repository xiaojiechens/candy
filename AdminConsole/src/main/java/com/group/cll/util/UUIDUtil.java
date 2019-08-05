package com.group.cll.util;

import java.util.UUID;
/**产生的UUID有这么长,通过截取和替换"-"后得到不同长度的UUID
 * 056085ce-8e46-492a-bcec-9a4d3690ce83
 * 8 + 4 + 4 + 4 + 12
 * @author 王俊	
 * @since 2017.07.21
 *
 */
public class UUIDUtil {
    public static void main(String[] args) {
        System.out.println(getUUID(37));
    }
    /**
     * 获得4个长度的十六进制的UUID
     * @return UUID
     */
    public static String get4UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[1];
        }
    /**
     * 获得8个长度的十六进制的UUID
     * @return UUID
     */
    public static String get8UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0];
    }
    /**
     * 获得12个长度的十六进制的UUID
     * @return UUID
     */
    public static String get12UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1];
    }
    /**
     * 获得16个长度的十六进制的UUID
     * @return UUID
     */
    public static String get16UUID(){

        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[2];
    }
    /**
     * 获得20个长度的十六进制的UUID
     * @return UUID
     */
    public static String get20UUID(){

        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[2]+idd[3];
    }
    /**
     * 获得24个长度的十六进制的UUID
     * @return UUID
     */
    public static String get24UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[4];
    }
    /**
     * 获得32个长度的十六进制的UUID
     * @return UUID
     */
    public static String get32UUID(){
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        return idd[0]+idd[1]+idd[2]+idd[3]+idd[4];
    }

    public static String getUUID(int digit){
    	UUID id=UUID.randomUUID();
    	
    	int a = digit/32;
    	int b = digit%32;

    	String uuid = id.toString().replace("-", "").substring(0, b);
    	
    	for(int i = 1 ; i <= a; i++) {
    		id=UUID.randomUUID();
    		uuid = uuid + id.toString().replace("-", "");
    	}
    	
    	return uuid;
    }

}

package my.subredes;

/**
 *
 * @author Primeiro PI
 */
public class Calculo {
    
    //Declaração das variáveis globais.
    
    static int[] ipValido;
    static int prefixo, hosts;
    static String classe, buffer, ipParcial, rede, broadcast, mascara;

    
    /**
     * Método responsável por verificar a autenticidade do IP digitado pelo usuário.
     * @param str
     * @return 
     */
    public boolean VerificaIP(String str) {
        //Variável local que armazenará o IP supostamente digitado pelo usuário.
        String[] splitIP;
        
        //Separa a string recebida em cada "."
        splitIP = str.split("\\.");
        
        //Se o comprimento do array obtido for diferente de 4 ou o primeiro elemento for 'zero', retorna false.
        if(splitIP.length != 4)return false;

        //Varre cada elemento do array gerado no split.
        for (String splitIP1 : splitIP) {
            //Se o elemento analisado for vazio ou mais do que 3, retorna false.
            if (splitIP1.equals("") || splitIP1.length() > 3) {
                return false;
            }
            //Varre o elemento do array separadamente.
            for (int j = 0; j < splitIP1.length(); j++) {
                //Se algum caracter não for número, retorna false.
                if (!Character.isDigit(splitIP1.charAt(j))) {
                    return false;
                }
            }
        }
        
        //Inicializa o vetor de inteiros que receberá os octetos.
        ipValido = new int[splitIP.length];
        
        for(int i = 0; i<splitIP.length; i++){
            //Verifica o valor de cada octeto.
            if(Integer.parseInt(splitIP[i])<0 || Integer.parseInt(splitIP[i])>255)return false;
                
            //Salva o valor do octeto no vetor de inteiros.
            ipValido[i] = Integer.parseInt(splitIP[i]);
        }
        //Se o IP passar por todas as verificações, o método retorna true.
        return true;
       
        
    }
    
    /**
     * Método responsável por verificar a autenticidade do prefixo digitado pelo usuário.
     * @param str
     * @return 
     */
    public boolean VerificaPrefixo(String str) {
        
        //Se o prefixo recebido for igual a 'zero' ou seu comprimento for maior que 2, retorna false.
        if(str.equals("") || str.length()>2)return false;
        
        //Verifica se algum caracter não é número.
        for(int i=0;i<str.length();i++)if(!Character.isDigit(str.charAt(i)))return false;
        
        //Verifica o valor do prefixo.
        int temp = Integer.parseInt(str);
        if(temp<1 || temp>32)return false;
        
        //Salva o valor do prefixo na variável global.
        prefixo = temp;
        return true;
    }
    
    /**
     * Método que é chamado pela classe Janela logo depois das verificações e
     * é responsável por gerenciar os cálculos e o envio das informações de volta
     * para a UI.
     * @return 
     */
    public String Retorno(){
        //Limpa a variável cujo conteúdo será enviado para a UI.
        buffer = "";
        //Método de verificação de classe.
        VerificaClasse();
        //Reune todas as informações necessárias para ser enviada.
        Imprime();
        
        //Retorno, em forma de String, que será recebida na UI e visualizada pelo usuário.
        return buffer;
    }
    
    /**
     * Método responsável por verificar a qual classe o IP pertence,
     * através da análise do primeiro octeto.
     */
    public void VerificaClasse() {
        //Salva o valor do primeiro octeto em uma variável local.
        int oct1 = ipValido[0];
        //Compara o valor e atribui uma classe ao IP.
        if(oct1<=127){
            classe = "A";
        }else if(oct1<=191){
            classe = "B";
        }else if(oct1<=223){
            classe = "C";
        }else if(oct1<=239){
            classe = "D";
        }else if(oct1<=255){
            classe = "E";
        }
    }
    
    /**
     * Método responsável por calcular o endereço de rede, o broadcast, a máscara
     * em formato decimal e salvar em variáveis globais que serão utilizadas depois.
     */
    public void Calcula(){
        //Variáveis locais.
        int mask = 0, aux1 = 0, aux=0, i = 7;
        //Quantidade de bits desligado na máscara.
        int bitsOff = 32-prefixo;
        
        //Número total de hosts.
        hosts = (int) Math.pow(2, bitsOff);
        
        
        //Cálculo do Broadcast. 
        do{
            aux+=hosts;
            aux1=aux-1;
        }while(aux1<ipValido[3]);
        
        //Calcula o valor da máscara em decimal.
        while(i>=bitsOff){
            mask+=Math.pow(2, i);
            i--;
        }
        
        //Atribuições variáveis globais com base nos cálculos realizados.
        ipParcial = ipValido[0]+"."+ipValido[1]+"."+ipValido[2]+".";
        rede = ipParcial+(aux1-hosts+1);
        broadcast = ipParcial+aux1;
        mascara = "255.255.255."+mask;
    }
    
    /**
     * Método responsável por imprimir as informações básicas e a tabela de sub-redes
     * de acordo com alguma condições estabelecidas.
     */
    public void Imprime(){
        //Se a classe for "C" e o prefixo estiver entre 24 e 31, as informações básicas e a tabela de sub-redes serão salvas na variável de retorno.
        if(classe.equals("C") && prefixo>=24 && prefixo<=31){
            Calcula();
            buffer ="IP Classe "+classe+"\nIP: "+ipParcial+ipValido[3]+"/"+prefixo+"\nMáscara: "+mascara+"\nRede: "+rede+"\nBroadcast: "+broadcast+"\n\n";
            buffer+=Tabela();
            }
        //Caso o prefixo ainda esteja no intervalo mas o IP não seja classe "C", somente as informações básicas serão salvas.
        else if(prefixo>=24 && prefixo<=31){
                Calcula();
                buffer ="IP Classe "+classe+"\nIP: "+ipParcial+ipValido[3]+"/"+prefixo+"\nMáscara: "+mascara+"\nRede: "+rede+"\nBroadcast: "+broadcast+"\n\n";
            }
        //Caso nenhumas das condições anteriores forem verdadeiras, somente o IP e o prefixo serão salvos.
        else{
                buffer = "IP Classe "+classe+"\nIP: "+ipValido[0]+"."+ipValido[1]+"."+ipValido[2]+"."+ipValido[3]+"/"+prefixo;
            }
    }
    
    
    
    /**
     * Método responsável por criar a tabela de sub-redes.
     * @return 
     */
    public String Tabela(){
        //Variáveis locais auxiliares.
        String temp="";
        int count = 1, octeto = 0, broadcastRede=hosts;
        
        //Laço que imprime a tabela das sub-redes.
        while(octeto<255){
            //Verifica se há a necessidade de apresentar o intervalo de hosts.
            if((octeto+1)==broadcastRede-1){
            temp+=count+": "+"Sub-rede "+ipParcial+octeto+"\tBroadcast: "+ipParcial+(broadcastRede-1)+"\n";    
            }else{
            temp+=count+": "+"Sub-rede "+ipParcial+octeto+"\tHost: "+ipParcial+(octeto+1)+" a "+(broadcastRede-2)+"\tBroadcast: "+ipParcial+(broadcastRede-1)+"\n";
            }
            broadcastRede+=hosts;
            octeto+=hosts;
            count++;
        }
        
        //Retorna a tabela completa e formatada.
        return temp;
    }
    }

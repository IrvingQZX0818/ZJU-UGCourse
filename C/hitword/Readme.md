
1. ��Ļ��ͬʱ��ʾ�ĵ��ʸ��������24��, ÿ�������ʾһ�����ʡ�
   ��Ϊ�ı�ģʽ������һ����25��, ����������ʾ�ɼ�, ����ֻʣ
	24�С�

2. ÿ����������ĸ���������ͬ, �����û����õ�һ����ĸʱ, 
   �����޷�ѡ��ð��ĸ����ʵ�����ĸͿ����ɫ��

3. �����ʿ����ڹ̶���25�������ѡȡ��Ҳ�����ú���read_dic()
   ���ֵ�dict.idx�������ȡ:

typedef struct 
{
   char entry[20];  /* ��Ŀ, �����20����ĸ; �����ʸպ�20����ĸʱ, ����'\0'���� */
   long lib_offset; /* ���ʽ��͵�ƫ����(��ƫ������ָ�ֵ������ļ�dict.lib�е�fseek����) */
   short int  xlat_len;   /* ���ʽ���ռ�õ��ֽ��� */
   short int  exam_len;   /* ��������ռ�õ��ֽ��� */
} IDX; 

IDX *pdic;        // �ֵ����׸�����ָ��
int entries;      // �ֵ��еĵ��ʸ���
WORD w[25];       // ��ʾ����Ļ�ϵĵ���
int index[26][2]; // index[i][0]: ('a'+i)��ĸ��ͷ���׸��������ֵ��е����
                  // index[i][1]: ('a'+i)��ĸ��ͷ�ĵ��ʸ���               
int screen_words; // ��Ļ������ʾ�ĵ�������
int hit=-1, hit_len=0; // hit=��ǰ���еĵ����±�, hit_len=�Ѵ��е���ĸ����
int stop = 0;
int words_list=0, words_hit=0, words_lost=0;
float hit_rate=0;

int read_dic(IDX **ppdic)
{
   FILE *fp;
   IDX *pdic;
   int len, entries;
   int i, begin;
   fp = fopen("dict.idx", "rb");
   if(fp == NULL)
      return 0;
   fseek(fp, 0, SEEK_END);
   len = ftell(fp);
   fseek(fp, 0, SEEK_SET);
   entries = len / sizeof(IDX);
   pdic = malloc(entries*sizeof(IDX));
   if(pdic == NULL)
      return 0;
   fread(pdic, sizeof(IDX), entries, fp);
   fclose(fp);
   *ppdic = pdic;
   begin = 0;
   for(i=0; i<26; i++)
   {
      while(begin < entries)
      {
         if((pdic[begin].entry[0] | 0x20) == 'a'+i)
            break;
         begin++;
      }
      index[i][0] = begin;
      if(i>0)
         index[i-1][1] = begin - index[i-1][0];
   }
   index[i-1][1] = entries - begin;
   return entries;
}

int all_alpha(char *p)
{
   int i, n=strlen(p);
   for(i=0; i<n; i++)
   {
      if(!islower(p[i]))
         break;
   }
   return i==n;
}

void generate_one_word(void)
{
   int i, j, d;
   int mark[26]={0};
   for(j=0; j<screen_words; j++)
   {
      mark[(w[j].word[0] | 0x20) - 'a'] = 1;
   }
   d = random(100) & 1;
   if(d==0)
   {
      for(j=0; j<26; j++)
         if(mark[j] == 0)
            break;
   }
   else
   {
      for(j=25; j>=0; j--)
         if(mark[j] == 0)
            break;
   }
   do
   {
      i = random(index[j][1])+index[j][0];
   } while(!all_alpha(pdic[i].entry));

   memset(w[screen_words].word, 0, sizeof(w[0].word));
   strncpy(w[screen_words].word, pdic[i].entry, 20);
   w[screen_words].y = 0;
   w[screen_words].x = random(80-strlen(w[screen_words].word));
   screen_words++;
   words_list++;
}

main()
{
   int key, i;
   randomize(); // �������ʼ��
   entries = read_dic(&pdic); // ��ȡ�ֵ����Ŀ
                              // entries=���ʸ���
                              // pdic->�׸�����
   memset(w, 0, sizeof(w));   // ��ʼ����Ļ�ϵ�25������
   screen_words = 0;          // ��Ļ������ʾ�ĵ��ʸ���
   clrscr(); // ����

   while(!stop)               // ����Escʱ, stop=1
   {
      generate_one_word();    // ����ĩβ����һ���µ���
      move_words_down();      // ��Ļ�ϸ����������ƶ�һ��
      show_score();           // ��ʾ�ɼ�
      delay(500);             // ��ʱ�ȴ�500����
      while(bioskey(1) != 0)  // if the keyboard buffer holds some keys
      {
         key = bioskey(0) & 0xFF; // convert scan code to ASCII
         if(key == 0x1B) // Esc is pressed
         {
            stop = 1;
            break;
         }
		   // �˴�ʡ�����ɴ���...
      } // while(bioskey(1) != 0)
   } // while(!stop)
   clrscr();
   puts("Done!");
   return 0;
}

#include <malloc.h>
#include <cstring>
#include "c_util.h"

namespace cutil {

    /**
     * 拼接字符串
     * @param c1
     * @param c2
     * @return
     */
    char* splicingString(const char *c1, const char *c2) {
        char *c3 = (char *) malloc(strlen(c1) + strlen(c2) + 1);
        for (int i = 0; c1[i] != '\0'; i++){
            c3[i] = c1[i];
        }
        for (int i = 0; c2[i] != '\0'; i++){
            c3[i + strlen(c1)] = c2[i];
        }
        c3[strlen(c1) + strlen(c2)] = '\0';
        return c3;
    }

}


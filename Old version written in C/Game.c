#include <SDL.h>
#include <SDL_image.h>
#include <stdio.h>
#include <string.h>

// Constants
const int screenwidth = 625;
const int screenheight = 400;
const int clipping = -1;

// File I/O
FILE* mapdat = NULL;

// SDL objects
SDL_Window* window = NULL;
SDL_Surface* surface = NULL;
SDL_Surface* tiles = NULL;
SDL_Surface* character = NULL;
SDL_Surface* optimizedSurface = NULL;
SDL_Surface* loadedSurface = NULL;

// Class definitions
typedef struct
{
    int height;
    int type;
    int selected;
}
Tile;

typedef struct
{
    Tile tiles[100][100];
    int gridsize;
    int offset[2];
}
Field;

typedef struct
{
    int location[2];
    int facingdir;
}
Player;

// Initialization
int init()
{
	int status = 0;

	if (SDL_Init(SDL_INIT_VIDEO) < 0)
	{
		printf("SDL could not initialize! SDL Error: %s\n", SDL_GetError());
		status = -1;
	}
	else
	{
		window = SDL_CreateWindow("Isometric Projection", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, screenwidth, screenheight, SDL_WINDOW_SHOWN);
		if (window == NULL)
		{
			printf("Window could not be created! SDL Error: %s\n", SDL_GetError());
			status = -1;
		}
		else
		{
			int imgFlags = IMG_INIT_PNG;
			if (!(IMG_Init(imgFlags) & imgFlags))
			{
				printf("SDL_image could not initialize! SDL_image Error: %s\n", IMG_GetError());
				status = -1;
			}
			else
			{
				surface = SDL_GetWindowSurface(window);
				SDL_FillRect(surface, NULL, SDL_MapRGB(surface->format, 0x66, 0x8B, 0x8B));
			}
		}
	}

	return status;
}

// Shutdown
void close()
{
    SDL_FreeSurface(tiles);
    tiles = NULL;
    SDL_FreeSurface(loadedSurface);
    SDL_FreeSurface(optimizedSurface);
	SDL_DestroyWindow(window);
	window = NULL;
	IMG_Quit();
	SDL_Quit();
}

// Image grabber
SDL_Surface* grabimage(char fname[20])
{
    char flocation[31] = "images/";
    strcat (flocation, fname);
    strcat (flocation, ".png");

	loadedSurface = IMG_Load(flocation);

	if(loadedSurface == NULL)
	{
		printf("Unable to load image %s! SDL_image Error: %s\n", flocation, IMG_GetError());
	}
	else
	{
		optimizedSurface = SDL_ConvertSurface(loadedSurface, surface->format, NULL);
		SDL_SetColorKey(optimizedSurface, SDL_TRUE, SDL_MapRGB(optimizedSurface->format, 0xFF, 0x00, 0x00));

		if(optimizedSurface == NULL)
		{
			printf("Unable to optimize image %s! SDL Error: %s\n", flocation, SDL_GetError());
		}
	}

	return optimizedSurface;
}

// One function to load them all
int loadmedia()
{
	int status = 0;

    tiles = grabimage("spritesheet");
    if(tiles == NULL)
    {
        printf("Failed to load spritesheet.png!\n");
        status = -1;
    }

    character = grabimage("character");
    if(tiles == NULL)
    {
        printf("Failed to load character.png!\n");
        status = -1;
    }

	return 0;
}

void redraw (Field field, Player player)
{
    int i;
    int j;
    int k;

    SDL_Rect spriteloc;
    SDL_Rect blitdest;
    spriteloc.y = 0;
    spriteloc.w = 40;
    spriteloc.h = 50;
    blitdest.w = 0;
    blitdest.h = 0;

    SDL_FillRect(surface, NULL, SDL_MapRGB(surface->format, 221, 221, 221));

    for (i = field.gridsize - 1 + field.offset[0]; i >= field.offset[0]; i--)
    {
       for (j = field.offset[1]; j < field.gridsize + field.offset[1]; j++)
        {
            spriteloc.y = 0;
            spriteloc.w = 40;
            spriteloc.h = 50;

            blitdest.x = 10 + (i - field.offset[0]) * 20 + (j - field.offset[1]) * 20;

            spriteloc.x = 0;
            for (k = -10; k < field.tiles[i][j].height; k += 5)
            {
                blitdest.y = 20 + (field.gridsize - 1 - i - field.offset[0]) * 10 + (j - field.offset[1]) * 10 - k + 20 * field.offset[0];
                SDL_BlitSurface(tiles, &spriteloc, surface, &blitdest);
            }

            spriteloc.x = field.tiles[i][j].type * 40;
            blitdest.y = 20 + (field.gridsize - 1 - i - field.offset[0]) * 10 + (j - field.offset[1]) * 10 - field.tiles[i][j].height + 20 * field.offset[0];

            SDL_BlitSurface(tiles, &spriteloc, surface, &blitdest);

            if (field.tiles[i][j].selected == 0)
            {
                spriteloc.y = 0;
                spriteloc.w = 40;
                spriteloc.h = 45;

                blitdest.y -= 5;

                switch (player.facingdir)
                {
                    case 0:
                        spriteloc.x = 80;
                    break;

                    case 1:
                        spriteloc.x = 40;
                    break;

                    case 2:
                        spriteloc.x = 0;
                    break;

                    case 3:
                        spriteloc.x = 120;
                    break;
                }
                SDL_BlitSurface(character, &spriteloc, surface, &blitdest);
            }
        }
    }
}

int main(int argc, char* args[])
{
	if(init() == -1)
	{
		printf("Failed to initialize!\n");
	}
	else
	{
	    if (loadmedia() == -1)
        {
            printf("Failed to load media!");
        }
        else
        {
            int i;
            int j;

            Field f;
            Player p;

            f.gridsize = 15;
            f.offset[0] = 0;
            f.offset[1] = 0;

            p.location[0] = 7;
            p.location[1] = 5;
            p.facingdir = 2;

            // f.tiles = (Tile**) malloc (f.gridsize * sizeof(Tile*));
            for (i = 0; i < 100; i++)
            {
                // f.tiles[i] = (Tile*) malloc (f.gridsize * sizeof(Tile));
                for (j = 0; j < 100; j++)
                {
                    f.tiles[i][j].selected = -1;
                    f.tiles[i][j].height = 0;
                    f.tiles[i][j].type = 0;
                }
            }

            f.tiles[7][5].selected = 0;

            int quit = 0;
            int lasttiletype = 0;
            SDL_Event e;

            mapdat = fopen("cfg/map.dat", "r");
            if (mapdat != NULL)
            {
                for (i = 0; i < 100; i++)
                {
                    for (j = 0; j < 100; j++)
                    {
                        if (fscanf(mapdat, "%d %d\n", &f.tiles[i][j].type, &f.tiles[i][j].height) == EOF)
                        {
                            printf("End of file has been reached.\n");
                        }
                    }
                }
            printf("Map data loaded.\n");
            }
            fclose(mapdat);

            while (quit >= 0)
            {
                while (SDL_PollEvent(&e)!= 0)
                {
                    if (e.type == SDL_QUIT)
                    {
                        quit = -1;
                    }
                    else if (e.type == SDL_KEYDOWN)
                    {
                        switch( e.key.keysym.sym )
                        {
                            case SDLK_UP:
                                p.facingdir = 0;
                                lasttiletype = f.tiles[p.location[0]][p.location[1]].type;
                                printf("%d\n", lasttiletype);
                                for (i = 0; i < 100; i++)
                                {
                                    for (j = 0; j < 100; j++)
                                    {
                                        if (clipping == 0)
                                        {
                                            if (f.tiles[i][j].selected == 0 && j > f.offset[1] && (f.tiles[i][j - 1].type < 7 || (f.tiles[i][j - 1].type > 9 && f.tiles[i][j - 1].type < 18)))
                                            {
                                                p.location[1] -= 1;
                                                f.tiles[i][j].selected = -1;
                                                f.tiles[i][j - 1].selected = 0;
                                                if (f.offset[1] > p.location[1] - 4 && f.offset[1] > 0)
                                                {
                                                    f.offset[1] -= 1;
                                                }
                                            }
                                        }
                                        else
                                        {
                                            if (f.tiles[i][j].selected == 0 && j > f.offset[1])
                                            {
                                                p.location[1] -= 1;
                                                f.tiles[i][j].selected = -1;
                                                f.tiles[i][j - 1].selected = 0;
                                                if (f.offset[1] > p.location[1] - 4 && f.offset[1] > 0)
                                                {
                                                    f.offset[1] -= 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            break;

                            case SDLK_DOWN:
                                p.facingdir = 2;
                                lasttiletype = f.tiles[p.location[0]][p.location[1]].type;
                                printf("%d\n", lasttiletype);
                                for (i = 0; i < 100; i++)
                                {
                                    for (j = 0; j < 100; j++)
                                    {
                                        if (clipping == 0)
                                        {
                                            if (f.tiles[i][j].selected == 0 && j < f.offset[1] + f.gridsize - 1 && (f.tiles[i][j + 1].type < 7 || (f.tiles[i][j + 1].type > 9 && f.tiles[i][j + 1].type < 18)))
                                            {
                                                p.location[1] += 1;
                                                f.tiles[i][j].selected = -1;
                                                f.tiles[i][j + 1].selected = 0;
                                                i = 100;
                                                j = 100;
                                                if (f.offset[1] + f.gridsize - 1 < p.location[1] + 4 && f.offset[1] < 84)
                                                {
                                                    f.offset[1] += 1;
                                                }
                                            }
                                        }
                                        else
                                        {
                                            if (f.tiles[i][j].selected == 0 && j < f.offset[1] + f.gridsize - 1)
                                            {
                                                p.location[1] += 1;
                                                f.tiles[i][j].selected = -1;
                                                f.tiles[i][j + 1].selected = 0;
                                                i = 100;
                                                j = 100;
                                                if (f.offset[1] + f.gridsize - 1 < p.location[1] + 4 && f.offset[1] < 84)
                                                {
                                                    f.offset[1] += 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            break;

                            case SDLK_LEFT:
                                p.facingdir = 1;

                                lasttiletype = f.tiles[p.location[0]][p.location[1]].type;

                                printf("%d\n", lasttiletype);
                                for (i = 0; i < 100; i++)
                                {
                                    for (j = 0; j < 100; j++)
                                    {
                                        if (clipping == 0)
                                        {
                                            if (f.tiles[i][j].selected == 0 && i > f.offset[0] && (f.tiles[i - 1][j].type < 7 || (f.tiles[i - 1][j].type > 9 && f.tiles[i - 1][j].type < 18)))
                                            {
                                                p.location[0] -= 1;
                                                f.tiles[i][j].selected = -1;
                                                f.tiles[i - 1][j].selected = 0;
                                                if (f.offset[0] > p.location[0] - 4 && f.offset[0] > 0)
                                                {
                                                    f.offset[0] -= 1;
                                                }
                                            }
                                        }
                                        else
                                        {
                                            if (f.tiles[i][j].selected == 0 && i > f.offset[0])
                                            {
                                                p.location[0] -= 1;
                                                f.tiles[i][j].selected = -1;
                                                f.tiles[i - 1][j].selected = 0;
                                                if (f.offset[0] > p.location[0] - 4 && f.offset[0] > 0)
                                                {
                                                    f.offset[0] -= 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            break;

                            case SDLK_RIGHT:
                                p.facingdir = 3;
                                lasttiletype = f.tiles[p.location[0]][p.location[1]].type;
                                printf("%d\n", lasttiletype);
                                for (i = 0; i < 100; i++)
                                {
                                    for (j = 0; j < 100; j++)
                                    {
                                        if (clipping == 0)
                                        {
                                            if (f.tiles[i][j].selected == 0 && i < f.offset[0] + f.gridsize - 1 && (f.tiles[i + 1][j].type < 7 || (f.tiles[i + 1][j].type > 9 && f.tiles[i + 1][j].type < 18)))
                                            {
                                                p.location[0] += 1;
                                                f.tiles[i][j].selected = -1;
                                                f.tiles[i + 1][j].selected = 0;
                                                i = 100;
                                                j = 100;
                                                if (f.offset[0] + f.gridsize - 1 < p.location[0] + 4 && f.offset[0] < 84)
                                                {
                                                    f.offset[0] += 1;
                                                }
                                            }
                                        }
                                        else
                                        {
                                            if (f.tiles[i][j].selected == 0 && i < f.offset[0] + f.gridsize - 1)
                                            {
                                                p.location[0] += 1;
                                                f.tiles[i][j].selected = -1;
                                                f.tiles[i + 1][j].selected = 0;
                                                i = 100;
                                                j = 100;
                                                if (f.offset[0] + f.gridsize - 1 < p.location[0] + 4 && f.offset[0] < 84)
                                                {
                                                    f.offset[0] += 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            break;

                            case SDLK_a:
                                if (f.tiles[p.location[0]][p.location[1]].type > 0 && clipping != 0)
                                {
                                    f.tiles[p.location[0]][p.location[1]].type -= 1;
                                    printf("%d\n", f.tiles[p.location[0]][p.location[1]].type);
                                }
                                else if (clipping == 0)
                                {
                                    switch (p.facingdir)
                                    {
                                        case 0:

                                        break;
                                        case 1:
                                        break;
                                        case 2:
                                        break;
                                        case 3:
                                        break;
                                    }
                                }
                            break;

                            case SDLK_w:
                                if (clipping != 0)
                                {
                                    f.tiles[p.location[0]][p.location[1]].height += 5;
                                }
                            break;

                            case SDLK_s:
                                if (clipping != 0)
                                {
                                    f.tiles[p.location[0]][p.location[1]].height -= 5;
                                }
                            break;

                            case SDLK_d:
                                if (f.tiles[p.location[0]][p.location[1]].type < 48 && clipping != 0)
                                {
                                    f.tiles[p.location[0]][p.location[1]].type += 1;
                                    printf("%d\n", f.tiles[p.location[0]][p.location[1]].type);
                                }
                            break;

                            case SDLK_r:
                                tiles = NULL;

                                if (loadmedia() == -1)
                                {
                                    printf("Failed to reload media using R function!\n");
                                }
                            break;

                            case SDLK_z:
                                if (clipping != 0)
                                {
                                    mapdat = fopen("cfg/map.dat", "w");
                                    if (mapdat != NULL && clipping != 0)
                                    {
                                        for (i = 0; i < 100; i++)
                                        {
                                            for (j = 0; j < 100; j++)
                                            {
                                                fprintf(mapdat, "%d %d\n", f.tiles[i][j].type, f.tiles[i][j].height);
                                            }
                                        }
                                        printf("Map data saved.\n");
                                    }
                                    fclose(mapdat);
                                }
                            break;

                            case SDLK_x:
                                if (clipping != 0)
                                {
                                    mapdat = fopen("cfg/map.dat", "r");
                                    if (mapdat != NULL)
                                    {
                                        for (i = 0; i < 100; i++)
                                        {
                                            for (j = 0; j < 100; j++)
                                            {
                                                if (fscanf(mapdat, "%d %d\n", &f.tiles[i][j].type, &f.tiles[i][j].height) == EOF)
                                                {
                                                    printf("End of file has been reached.\n");
                                                }
                                            }
                                        }
                                        printf("Map data loaded.\n");
                                    }
                                    fclose(mapdat);
                                }
                            break;

                            case SDLK_b:
                                if (clipping != 0)
                                {
                                    mapdat = fopen("cfg/mapbackup.dat", "w");
                                    if (mapdat != NULL)
                                    {
                                        for (i = 0; i < 100; i++)
                                        {
                                            for (j = 0; j < 100; j++)
                                            {
                                                fprintf(mapdat, "%d %d\n", f.tiles[i][j].type, f.tiles[i][j].height);
                                            }
                                        }
                                        printf("Map data backup has been made.\n");
                                    }
                                    fclose(mapdat);
                                }
                            break;

                            /*case SDLK_j:
                                if (f.offset[0] > 0)
                                {
                                    f.offset[0] -= 1;
                                    printf("offset1 is %d\n", f.offset[0]);
                                    if (f.offset[0] + f.gridsize - 1 < p.location[0])
                                    {
                                        f.tiles[p.location[0]][p.location[1]].selected = -1;
                                        p.location[0] -= 1;
                                        f.tiles[p.location[0]][p.location[1]].selected = 0;
                                    }
                                }
                            break;

                            case SDLK_k:
                                if (f.offset[1] < 84)
                                {
                                    f.offset[1] += 1;
                                    printf("offset2 is %d\n", f.offset[1]);
                                    if (f.offset[1] > p.location[1])
                                    {
                                        f.tiles[p.location[0]][p.location[1]].selected = -1;
                                        p.location[1] += 1;
                                        f.tiles[p.location[0]][p.location[1]].selected = 0;
                                    }
                                }
                            break;

                            case SDLK_l:
                                if (f.offset[0] < 84)
                                {
                                    f.offset[0] += 1;
                                    printf("offset1 is %d\n", f.offset[0]);
                                    if (f.offset[0] > p.location[0])
                                    {
                                        f.tiles[p.location[0]][p.location[1]].selected = -1;
                                        p.location[0] += 1;
                                        f.tiles[p.location[0]][p.location[1]].selected = 0;

                                    }
                                }
                            break;

                            case SDLK_i:
                                if (f.offset[1] > 0)
                                {
                                    f.offset[1] -= 1;
                                    printf("offset2 is %d\n", f.offset[1]);
                                    if (f.offset[1] + f.gridsize - 1 < p.location[1])
                                    {
                                        f.tiles[p.location[0]][p.location[1]].selected = -1;
                                        p.location[1] -= 1;
                                        f.tiles[p.location[0]][p.location[1]].selected = 0;

                                    }
                                }
                            break;*/

                            case SDLK_t:
                                if (clipping != 0)
                                {
                                    f.tiles[p.location[0]][p.location[1]].type = lasttiletype;
                                }
                            break;

                            default:
                            break;
                        }
                    }
                }
                redraw(f, p);
                SDL_UpdateWindowSurface(window);
                SDL_Delay(50);
            }
        }
	}

	close();

	return 0;
}

/*
  blocks_chalk_drip.c

  Blocks, Chalk and Drip Magic Tools Plugin
  Tux Paint - A simple drawing program for children.

  Copyright (c) 2002-2021 by Bill Kendrick and others; see AUTHORS.txt
  bill@newbreedsoftware.com
  http://www.tuxpaint.org/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
  (See COPYING.txt)

  Last updated: November 8, 2021
  $Id$
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "tp_magic_api.h"
#include "SDL_image.h"

/* What tools we contain: */

enum
{
  TOOL_BLOCKS,
  TOOL_CHALK,
  TOOL_DRIP,
  NUM_TOOLS
};

#define EFFECT_REZ 4


/* Our globals: */

static Mix_Chunk *snd_effect[NUM_TOOLS];


/* Our function prototypes: */

int blocks_chalk_drip_init(magic_api * api);
Uint32 blocks_chalk_drip_api_version(void);
int blocks_chalk_drip_get_tool_count(magic_api * api);
SDL_Surface *blocks_chalk_drip_get_icon(magic_api * api, int which);
char *blocks_chalk_drip_get_name(magic_api * api, int which);
int blocks_chalk_drip_get_group(magic_api * api, int which);
char *blocks_chalk_drip_get_description(magic_api * api, int which, int mode);
static void blocks_chalk_drip_linecb(void *ptr, int which, SDL_Surface * canvas, SDL_Surface * last, int x, int y);
void blocks_chalk_drip_drag(magic_api * api, int which, SDL_Surface * canvas,
                            SDL_Surface * last, int ox, int oy, int x, int y, SDL_Rect * update_rect);
void blocks_chalk_drip_click(magic_api * api, int which, int mode,
                             SDL_Surface * canvas, SDL_Surface * last, int x, int y, SDL_Rect * update_rect);
void blocks_chalk_drip_release(magic_api * api, int which,
                               SDL_Surface * canvas, SDL_Surface * last, int x, int y, SDL_Rect * update_rect);
void blocks_chalk_drip_shutdown(magic_api * api);
void blocks_chalk_drip_set_color(magic_api * api, Uint8 r, Uint8 g, Uint8 b);
int blocks_chalk_drip_requires_colors(magic_api * api, int which);
void blocks_chalk_drip_switchin(magic_api * api, int which, int mode, SDL_Surface * canvas);
void blocks_chalk_drip_switchout(magic_api * api, int which, int mode, SDL_Surface * canvas);
int blocks_chalk_drip_modes(magic_api * api, int which);



int blocks_chalk_drip_init(magic_api * api)
{
  char fname[1024];

  snprintf(fname, sizeof(fname), "%ssounds/magic/blocks.wav", api->data_directory);
  snd_effect[0] = Mix_LoadWAV(fname);

  snprintf(fname, sizeof(fname), "%ssounds/magic/chalk.wav", api->data_directory);
  snd_effect[1] = Mix_LoadWAV(fname);

  snprintf(fname, sizeof(fname), "%ssounds/magic/drip.wav", api->data_directory);
  snd_effect[2] = Mix_LoadWAV(fname);

  return (1);
}

Uint32 blocks_chalk_drip_api_version(void)
{
  return (TP_MAGIC_API_VERSION);
}


// We have multiple tools:
int blocks_chalk_drip_get_tool_count(magic_api * api ATTRIBUTE_UNUSED)
{
  return (NUM_TOOLS);
}

// Load our icons:
SDL_Surface *blocks_chalk_drip_get_icon(magic_api * api, int which)
{
  char fname[1024];

  if (which == TOOL_BLOCKS)
    {
      snprintf(fname, sizeof(fname), "%simages/magic/blocks.png", api->data_directory);
    }
  else if (which == TOOL_CHALK)
    {
      snprintf(fname, sizeof(fname), "%simages/magic/chalk.png", api->data_directory);
    }
  else if (which == TOOL_DRIP)
    {
      snprintf(fname, sizeof(fname), "%simages/magic/drip.png", api->data_directory);
    }

  return (IMG_Load(fname));
}

// Return our names, localized:
char *blocks_chalk_drip_get_name(magic_api * api ATTRIBUTE_UNUSED, int which)
{
  if (which == TOOL_BLOCKS)
    return (strdup(gettext_noop("Blocks")));
  else if (which == TOOL_CHALK)
    return (strdup(gettext_noop("Chalk")));
  else if (which == TOOL_DRIP)
    return (strdup(gettext_noop("Drip")));

  return (NULL);
}

// Return our group (all the same):
int blocks_chalk_drip_get_group(magic_api * api ATTRIBUTE_UNUSED, int which ATTRIBUTE_UNUSED)
{
  return MAGIC_TYPE_DISTORTS;
}

// Return our descriptions, localized:
char *blocks_chalk_drip_get_description(magic_api * api ATTRIBUTE_UNUSED, int which, int mode)
{
  if (which == TOOL_BLOCKS)
    {
      if (mode == MODE_PAINT)
        {
          return (strdup(gettext_noop("Click and drag the mouse around to make the picture blocky.")));
        }
      else
        {
          return (strdup(gettext_noop("Click to make the entire picture blocky.")));
        }
    }
  else if (which == TOOL_CHALK)
    {
      if (mode == MODE_PAINT)
        {
          return (strdup(gettext_noop("Click and drag the mouse around to turn the picture into a chalk drawing.")));
        }
      else
        {
          return (strdup(gettext_noop("Click to turn the entire picture into a chalk drawing.")));
        }
    }
  else if (which == TOOL_DRIP)
    {
      return (strdup(gettext_noop("Click and drag the mouse around to make the picture drip.")));
    }

  return (NULL);
}

// Do the effect:

static void blocks_chalk_drip_linecb(void *ptr, int which, SDL_Surface * canvas, SDL_Surface * last, int x, int y)
{
  magic_api *api = (magic_api *) ptr;
  int xx, yy;
  int h;
  SDL_Rect src, dest;
  Uint8 r, g, b;
  Uint32 colr;

  if (which == TOOL_BLOCKS)
    {
      /* Put x/y on exact grid points: */

      x = (x / EFFECT_REZ) * EFFECT_REZ;
      y = (y / EFFECT_REZ) * EFFECT_REZ;

      if (!api->touched(x, y))
        {
          for (yy = y - (EFFECT_REZ * 2); yy < y + (EFFECT_REZ * 2); yy = yy + EFFECT_REZ)
            {
              for (xx = x - (EFFECT_REZ * 2); xx < x + (EFFECT_REZ * 2); xx = xx + EFFECT_REZ)
                {
                  Uint32 pix[(EFFECT_REZ * EFFECT_REZ)];
                  Uint32 p_or = 0;
                  Uint32 p_and = ~0;
                  unsigned i = (EFFECT_REZ * EFFECT_REZ);

                  while (i--)
                    {
                      Uint32 p_tmp;

                      p_tmp = api->getpixel(last, xx + (i >> 2), yy + (i & 3));
                      p_or |= p_tmp;
                      p_and &= p_tmp;
                      pix[i] = p_tmp;
                    }
                  if (p_or == p_and)    // if all pixels the same already
                    {
                      SDL_GetRGB(p_or, last->format, &r, &g, &b);
                    }
                  else          // nope, must average them
                    {
                      double r_sum = 0.0;
                      double g_sum = 0.0;
                      double b_sum = 0.0;

                      i = (EFFECT_REZ * EFFECT_REZ);
                      while (i--)
                        {
                          SDL_GetRGB(pix[i], last->format, &r, &g, &b);
                          r_sum += api->sRGB_to_linear(r);
                          g_sum += api->sRGB_to_linear(g);
                          b_sum += api->sRGB_to_linear(b);
                        }
                      r = api->linear_to_sRGB(r_sum / (float) (EFFECT_REZ * EFFECT_REZ));
                      g = api->linear_to_sRGB(g_sum / (float) (EFFECT_REZ * EFFECT_REZ));
                      b = api->linear_to_sRGB(b_sum / (float) (EFFECT_REZ * EFFECT_REZ));
                    }

                  /* Draw block: */

                  dest.x = xx;
                  dest.y = yy;
                  dest.w = EFFECT_REZ;
                  dest.h = EFFECT_REZ;

                  SDL_FillRect(canvas, &dest, SDL_MapRGB(canvas->format, r, g, b));
                }
            }
        }
    }
  else if (which == TOOL_CHALK)
    {
      for (yy = y - (EFFECT_REZ * 2); yy <= y + (EFFECT_REZ * 2); yy = yy + EFFECT_REZ)
        {
          for (xx = x - (EFFECT_REZ * 2); xx <= x + (EFFECT_REZ * 2); xx = xx + EFFECT_REZ)
            {
              dest.x = xx + ((rand() % (EFFECT_REZ + 1)) - (EFFECT_REZ / 2));
              dest.y = yy + ((rand() % (EFFECT_REZ + 1)) - (EFFECT_REZ / 2));
              dest.w = (rand() % EFFECT_REZ) + (EFFECT_REZ / 2);
              dest.h = (rand() % EFFECT_REZ) + (EFFECT_REZ / 2);

              colr = api->getpixel(last, clamp(0, xx, canvas->w - 1), clamp(0, yy, canvas->h - 1));
              SDL_FillRect(canvas, &dest, colr);
            }
        }
    }
  else if (which == TOOL_DRIP)
    {
      for (xx = x - 8; xx <= x + 8; xx++)
        {
          h = (rand() % 8) + 8;

          for (yy = y; yy <= y + h; yy++)
            {
              src.x = xx;
              src.y = y;
              src.w = 1;
              src.h = 16;

              dest.x = xx;
              dest.y = yy;

              SDL_BlitSurface(last, &src, canvas, &dest);
            }
        }
    }
}

// Affect the canvas on drag:
void blocks_chalk_drip_drag(magic_api * api, int which, SDL_Surface * canvas,
                            SDL_Surface * last, int ox, int oy, int x, int y, SDL_Rect * update_rect)
{
  api->line((void *)api, which, canvas, last, ox, oy, x, y, 1, blocks_chalk_drip_linecb);

  if (ox > x)
    {
      int tmp = ox;

      ox = x;
      x = tmp;
    }
  if (oy > y)
    {
      int tmp = oy;

      oy = y;
      y = tmp;
    }

  update_rect->x = ox - 16;
  update_rect->y = oy - 16;
  update_rect->w = (x + 16) - update_rect->x;
  update_rect->h = (y + 16) - update_rect->y;

  api->playsound(snd_effect[which], (x * 255) / canvas->w, 255);
}

// Affect the canvas on click:
void blocks_chalk_drip_click(magic_api * api, int which, int mode,
                             SDL_Surface * canvas, SDL_Surface * last, int x, int y, SDL_Rect * update_rect)
{
  if (mode == MODE_PAINT) {
    blocks_chalk_drip_drag(api, which, canvas, last, x, y, x, y, update_rect);
  } else /* MODE_FULLSCREEN */ {
    for (y = 0; y < canvas->h; y += EFFECT_REZ) {
      if (y % 10 == 0) {
        api->update_progress_bar();
      }

      for (x = 0; x < canvas->w; x += EFFECT_REZ) {
        blocks_chalk_drip_linecb(api, which, canvas, last, x, y);
      }
    }
    update_rect->x = 0;
    update_rect->y = 0;
    update_rect->w = canvas->w;
    update_rect->h = canvas->h;

    api->playsound(snd_effect[which], (x * 255) / canvas->w, 255);
  }
}

// Affect the canvas on release:
void blocks_chalk_drip_release(magic_api * api ATTRIBUTE_UNUSED, int which ATTRIBUTE_UNUSED,
                               SDL_Surface * canvas ATTRIBUTE_UNUSED, SDL_Surface * last ATTRIBUTE_UNUSED,
                               int x ATTRIBUTE_UNUSED, int y ATTRIBUTE_UNUSED, SDL_Rect * update_rect ATTRIBUTE_UNUSED)
{
}

// No setup happened:
void blocks_chalk_drip_shutdown(magic_api * api ATTRIBUTE_UNUSED)
{
  if (snd_effect[0] != NULL)
    Mix_FreeChunk(snd_effect[0]);

  if (snd_effect[1] != NULL)
    Mix_FreeChunk(snd_effect[1]);
}

// Record the color from Tux Paint:
void blocks_chalk_drip_set_color(magic_api * api ATTRIBUTE_UNUSED,
                                 Uint8 r ATTRIBUTE_UNUSED, Uint8 g ATTRIBUTE_UNUSED, Uint8 b ATTRIBUTE_UNUSED)
{
}

// Use colors:
int blocks_chalk_drip_requires_colors(magic_api * api ATTRIBUTE_UNUSED, int which ATTRIBUTE_UNUSED)
{
  return 0;
}

void blocks_chalk_drip_switchin(magic_api * api ATTRIBUTE_UNUSED, int which ATTRIBUTE_UNUSED, int mode ATTRIBUTE_UNUSED,
                                SDL_Surface * canvas ATTRIBUTE_UNUSED)
{
}

void blocks_chalk_drip_switchout(magic_api * api ATTRIBUTE_UNUSED, int which ATTRIBUTE_UNUSED,
                                 int mode ATTRIBUTE_UNUSED, SDL_Surface * canvas ATTRIBUTE_UNUSED)
{
}

int blocks_chalk_drip_modes(magic_api * api ATTRIBUTE_UNUSED, int which)
{
  if (which == TOOL_BLOCKS || TOOL_CHALK) {
    return (MODE_PAINT | MODE_FULLSCREEN);
  } else /* TOOL_DRIP */ {
    return (MODE_PAINT);
  }
}

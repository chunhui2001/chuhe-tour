import { trigger, state, animate, style, transition } from '@angular/core';

// Angular2 -- Animating Router transitions

export function moveIn() {
  return trigger('moveIn', [
    state('void', style({ position: 'fixed', with: '100%' })),
    state('*', style({position: 'fixed', with: '100%'})),
    transition(':enter', [
      style({ opacity: '0', transform: 'translateX(100px)' } ),
      animate( '.6s ease-in-out', style({opacity: '1', transform: 'translateX(0)'}))
    ]),
    transition(':leave', [
      style({ opacity: '1', transform: 'translateX(0)' } ),
      animate( '.3s ease-in-out', style({opacity: '0', transform: 'translateX(-200px)'}))
    ])
  ]);
}

export function moveInLeft() {
  return trigger('moveInLeft', [
    transition(':enter', [
      style({ opacity: '0', transform: 'translateY(-100px)' } ),
      animate( '.6s .2s ease-in-out', style({opacity: '1', transform: 'translateY(0)'}))
    ])
  ]);
}

export function fallIn() {
  return trigger('fallIn', [
    transition(':enter', [
      style({ opacity: '0', transform: 'translateY(40px)' } ),
      animate( '.4s .2s ease-in-out', style({opacity: '1', transform: 'translateY(0)'}))
    ]),
    transition(':leave', [
      style({ opacity: '1', transform: 'translateX(0)' } ),
      animate( '.3s ease-in-out', style({opacity: '0', transform: 'translateX(-200px)'}))
    ])
  ]);
}


export function comp() {
  return trigger('comp', [
    transition(':enter', [
      style({ display: 'inline-block' } )
    ]),
    transition(':leave', [
      style({ display: 'inline-block' } )
    ])
  ]);
}